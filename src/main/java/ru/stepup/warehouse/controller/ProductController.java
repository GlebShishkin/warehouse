package ru.stepup.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.stepup.warehouse.entity.InstanceArrangement;
import ru.stepup.warehouse.entity.Product;
import ru.stepup.warehouse.entity.ProductRegister;
import ru.stepup.warehouse.enumerator.Status;
import ru.stepup.warehouse.exceptions.DoubleException;
import ru.stepup.warehouse.exceptions.NotFoundException;
import ru.stepup.warehouse.repository.InstanceArrangementRepo;
import ru.stepup.warehouse.repository.ProductRegisterRepo;
import ru.stepup.warehouse.repository.ProductRepo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class ProductController {
    @Autowired
    private ProductRegisterRepo productRegisterRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private InstanceArrangementRepo instanceArrangementRepo;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/corporate_settlemet_instance/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String addProduct(@RequestBody Product product) {

        log.info("product: getInstanceid() = " + product.getInstanceId() + "; getContractDate() = " + product.getContractDate()
                + "; instanceArrangementList.size() = " + product.getInstanceArrangement().size());

        List<Integer> registerListId = new ArrayList<>();   // список id пеестров для вывода в json

        if (product.getInstanceId() == null) // Шаг 1
        {
            // ТЗ: Если ИД ЭП в поле Request.Body.instanceId не задано (NULL/Пусто), то выполняется процесс создания нового
            // экземпляра • Перейти на шаг 1.1

            // ТЗ: Шаг 1.1
            // Проверка таблицы ЭП (tpp_product) на дубли. Для этого необходимо отобрать строки по условию
            //tpp_product.number == Request.Body.ContractNumber. Если результат отбора не пуст, значит имеются повторы
            Integer prodId = productRepo.findProductId(product.getContractNumber());
            if (prodId != null) {
                // ТЗ "Если записи найдены • вернуть Статус: 400/Bad Request, Текст: Параметр № Дополнительного соглашения (сделки) Number
                //<значение> уже существует для ЭП с ИД <значение1>"
                throw new DoubleException("Параметр № Дополнительного соглашения (сделки) Number " + product.getContractNumber() + " уже существует для ЭП с ИД " + prodId);
            }

            // Шаг 1.2 Проверка таблицы ДС (agreement) на дубли. Отобрать записи по условию agreement.number == Request.Body.Arrangement[N].Number
            // Если записи найдены • вернуть Статус: 400/Bad Request, Текст: Параметр № Дополнительного соглашения (сделки) Number
            // <значение> уже существует для ЭП с ИД <значение1>. Если записей нет • перейти на Шаг 1.3
            for (InstanceArrangement arrangement:product.getInstanceArrangement()) {
                if (0 < productRepo.findAgreementDouble(arrangement.getNumber())) {
                    // ТЗ "Если записи найдены • вернуть Статус: 400/Bad Request, Текст: Параметр № Дополнительного соглашения (сделки) Number
                    //<значение> уже существует для ЭП с ИД <значение1>"
                    throw new DoubleException("Параметр № " + arrangement.getNumber() + " уже существует для ЭП с ИД " + product.getInstanceId());
                };
            }

            // ТЗ: Шаг 1.3 По КодуПродукта найти связные записи в Каталоге Типа регистра
            // Request.Body.ProductCode == tpp_ref_product_class.value среди найденных записей отобрать те, у которых
            // tpp_ref_product_register_type.account_type имеет значение “Клиентский”. Если найдено одна или более записей
            // Запомнить найденные registerType с целью добавления соответствующего числа строк в таблицу ПР (tpp_product_registry)
            List<String> registerTypeList = productRepo.findRegisterType(product.getProductCode());
            log.info("product.getProductCode() = " + product.getProductCode() + "; registerTypeList.size = " + registerTypeList.size());

            // ТЗ: Шаг 1.4 Добавить строку в таблицу tpp_product, заполнить согласно Request.Body:
            // Сформировать/Запомнить новый ИД ЭП tpp_product.id
            log.info("Новый ИД ЭП tpp_product.id " + productRepo.save(product));

            // Шаг 1.5 Добавить в таблицу ПР (tpp_product_registry) строки, заполненные:
            // • Id - ключ таблицы
            // • product_id - ссылка на таблицу ЭП, где tpp_product.id == tpp_product_registry.product_id
            // • type – тип ПР (лицевого/внутрибанковского счета)
            // • account_id – ид счета
            // • currency_code – код валюты счета
            // • state – статус счета, enum (0, Закрыт/1, Открыт/2, Зарезервирован/3, Удалён
            for (String registerType:registerTypeList) {
                ProductRegister productRegister = new ProductRegister();
                productRegister.setInstanceid(BigInteger.valueOf(product.getId()));
                productRegister.setRegistryTypeCode(registerType);
                productRegister.setAccount_number("");  //??? в Body "corporate_settlemet_instance/create" не хватает критериев для поиска
                productRegister.setCurrencyCode(product.getCurrencyCode());
                productRegister.setState(Status.OPEN);
                log.info("добавляем запись в tpp_product_registry " + productRegisterRepo.save(productRegister));
                registerListId.add(productRegister.getId());    // для вывода s Response.Body
            }
        }
        else    // Шаг 2: Request.Body.instanceId не пустое -> изменяется состав ДС
        {
            // Шаг 2.1
            // Проверка таблицы ЭП (tpp_product) на существование ЭП. Для этого необходимо отобрать строки по условию
            // tpp_product.id == Request.Body.instanceId
            // Если запись не найдена • вернуть Статус: 404/Not Found, Текст: Экземпляр продукта с параметром instanceId <значение> не найден.
            if (productRepo.checkProduct(product.getInstanceId()) == null) {
                throw new NotFoundException("Экземпляр продукта с параметром instanceId " + product.getInstanceId() + " не найден ");
            }

            // Шаг 2.2 Проверка таблицы ДС (agreement) на дубли
            // Отобрать записи по условию agreement.number == Request.Body.Arrangement[N].Numbe
            for (InstanceArrangement arrangement:product.getInstanceArrangement()) {
                if (0 < productRepo.findAgreementDouble(arrangement.getNumber())) {
                    // ТЗ "Если записи найдены • вернуть Статус: 400/Bad Request, Текст: Параметр № Дополнительного соглашения (сделки) Number
                    //<значение> уже существует для ЭП с ИД <значение1>"
                    throw new DoubleException("Параметр № " + arrangement.getNumber() + " уже существует для ЭП с ИД " + product.getInstanceId());
                };
            }
        }

        // Шаг 8.
        // • Добавить строку в таблицу ДС (agreement)
        // • заполнить соотв. поля ДС согласно составу Request.Body, см. массив Arrangement[…]
        // • сформировать agreement.Id , связать с таблицей ЭП по ИД ЭП, где tpp_product.id == agreement.product_id
        for (InstanceArrangement arrangement:product.getInstanceArrangement()) {
            arrangement.setProduct_id(product.getId());
            instanceArrangementRepo.save(arrangement);  // сохраняем Request.Body.Arrangement в таблице
        }

        /*ТЗ: 6.3. Формат ответа
        Response.Body
        {
            "data": {
            "instanceId": "string", // Идентификатор экземпляра продукта, при Status <> 200 OK может быть NULL
                    "registerId": [ // Идентификатор продуктового регистра, массив, при Status <> 200 OK может быть пуст
            "registerId1", … " registerIdN"
            ],
            "supplementaryAgreementId": [ //ID доп.соглашения, при Status <> 200 OK может быть пуст
            " supplementaryAgreementId1 ", … " supplementaryAgreementIdN"
            ]
            }
        }*/
        JSONObject resultJson = new JSONObject();
        JSONObject item = new JSONObject();
        item.put("instanceId", product.getId().toString());
        item.put("registerId", new JSONArray(registerListId.stream().map(x->x.toString()).toList()));
//        item.put("supplementaryAgreementId", product.getInstanceArrangement().stream().map(x -> x.getSupplementaryAgreementId()).toList());
        List<String> list = new ArrayList<>();
        for (InstanceArrangement agreement:product.getInstanceArrangement() ) {
            list.add(agreement.getId().toString());
        }
        item.put("supplementaryAgreementId", new JSONArray(list));
        resultJson.put("data", item);
        return resultJson.toString();
    }
}
