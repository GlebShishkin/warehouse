package ru.stepup.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.stepup.warehouse.entity.ProductRegister;
import ru.stepup.warehouse.enumerator.Status;
import ru.stepup.warehouse.exceptions.DoubleException;
import ru.stepup.warehouse.exceptions.NotFoundException;
import ru.stepup.warehouse.repository.InstanceArrangementRepo;
import ru.stepup.warehouse.repository.ProductRegisterRepo;
import ru.stepup.warehouse.repository.ProductRepo;

@Slf4j
@RestController
public class ProductRegisterController {
    @Autowired
    private ProductRegisterRepo productRegisterRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private InstanceArrangementRepo instanceArrangementRepo;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(path = "/corporate_settlemet_account/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String addProductRegister(@RequestBody ProductRegister productRegister) {

        log.info("!!! addProductRegister: productRegister.product_id = " + productRegister.getId());

        // Шаг 2
        int cnt = productRegisterRepo.findProductRegistryDouble(productRegister.getInstanceid(), productRegister.getRegistryTypeCode());
        log.info("cnt = " + cnt);
        if (0 < cnt) {
            throw new DoubleException("Параметр getRegistryTypeCode тип регистра " + productRegister.getRegistryTypeCode() + "' уже существует для ЭП с ИД '" + productRegister.getInstanceid() + "'");
        };

        // Шаг 3
        productRegisterRepo.findRegistryTypeCode(productRegister.getRegistryTypeCode())
                .stream().findAny().orElseThrow(
                        () -> new NotFoundException("Код продукта '" + productRegister.getRegistryTypeCode() + "' не найден в каталоге продуктов")
                );

        // Шаг 4
        productRegister.setAccount_number(
                productRegisterRepo.findAccount(productRegister.getBranchCode()
                        , productRegister.getCurrencyCode()
                        , productRegister.getMdmCode()
                        , productRegister.getPriorityCode()
                        , productRegister.getRegistryTypeCode()
                ).stream().findFirst().orElseThrow(
                        () -> new NotFoundException("Ошибка при поиске счета")
                )
        );

        productRegister.setState(Status.OPEN);

        log.info("New row " + productRegisterRepo.save(productRegister));

        JSONObject resultJson = new JSONObject();
        return resultJson.put("data", new JSONObject().put("accountid", productRegister.getId())).toString();
    }
}
