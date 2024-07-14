package ru.stepup.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.stepup.warehouse.entity.InstanceArrangement;
import ru.stepup.warehouse.entity.Product;

import java.math.BigInteger;
import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Integer> {

    // Шаг 1.1 Проверка таблицы ЭП (tpp_product) на дубли. Для этого необходимо отобрать строки по условию
    // tpp_product.number == Request.Body.ContractNumber
    @Query(value = "select t.id from tpp_product t where t.number = ?1 LIMIT 1", nativeQuery = true)
    Integer findProductId(String contractNumber);

    // ТЗ: Шаг 1.2  Проверка таблицы ДС (agreement) на дубли Отобрать записи по условию agreement.number == Request.Body.Arrangement[N].Number
    // Если записи найдены • вернуть Статус: 400/Bad Request, Текст: Параметр № Дополнительного соглашения (сделки) Number
    // <значение> уже существует для ЭП с ИД <значение1>. Если записей нет • перейти на Шаг 1.3
    @Query(value = "select count(*) from agreement t where t.number = ?1", nativeQuery = true)
    int findAgreementDouble(String arrangementNumber);

    // ТЗ: Шаг 1.3 По КодуПродукта найти связные записи в Каталоге Типа регистра
    // Request.Body.ProductCode == tpp_ref_product_class.value
    // среди найденных записей отобрать те, у которых
    // tpp_ref_product_register_type.account_type имеет значение “Клиентский”
    // Если найдено одна или более записей
    // Запомнить найденные registerType с целью добавления соответствующего числа строк в таблицу ПР (tpp_product_registry)
    @Query(value = "SELECT typ.value FROM tpp_ref_product_register_type typ inner join tpp_ref_product_class cls " +
            "on typ.product_class_code = cls.value " +
            "where cls.value = ?1 and typ.account_type = 'Клиентский'", nativeQuery = true)
    List<String> findRegisterType(String ProductCode);

    // Шаг 2.1
    // Проверка таблицы ЭП (tpp_product) на существование ЭП. Для этого необходимо отобрать строки по условию
    // tpp_product.id == Request.Body.instanceId
    @Query(value = "select t.id from tpp_product t where t.id = ?1", nativeQuery = true)
    Integer checkProduct(BigInteger instanceid);

}
