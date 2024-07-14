package ru.stepup.warehouse.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

// непонятная структура для "corporate_settlemet_instance/create" - поставляется в @RequestBody, но нигде не сохраняется (видимо заготовка
// на будущее)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class AdditionalPropertiesVip implements Serializable {
    private String key;
    private String value;
    private String name;
}
