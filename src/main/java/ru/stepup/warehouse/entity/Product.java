package ru.stepup.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.stepup.warehouse.enumerator.ProdType;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("data")   // используем для создания узла json - "data":
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME) // используем для создания узла json - "data":
@Table(name = "tpp_product")
public class Product {

    @Transient
    private List<AdditionalPropertiesVip> additionalPropertiesVip;

    @Transient
    private List<InstanceArrangement> instanceArrangement;  // список соглашений из @RequestBody, кот. сохраним в таблице "agreement"

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)    // значение будет сохранено в базу как строка
    @Column(name="type")
    private ProdType productType;

    @Transient
    @JsonProperty("productCode")
    @NotEmpty(message = "поле productCode не может быть пустым")
    private String productCode; // ТЗ: По КодуПродукта найти связные записи в Каталоге Типа регистра:  Request.Body.ProductCode == tpp_ref_product_class.value

    @Transient
    private String mdmCode; // Код Клиента (mdm)

    @Transient
    private String BranchCode;

    @NotEmpty(message = "поле contractNumber не может быть пустым")
    @Column(name="number")
    private String contractNumber;  // ТЗ: "Проверка таблицы ЭП (tpp_product) на дубли. tpp_product.number == Request.Body.ContractNumber"

    @Transient
    @NotNull(message = "поле contractDate не может быть пустым")
    private Date contractDate;  // Дата заключения договора обслуживания

    @NotNull(message = "поле priority не может быть пустым")
    @Column(name="priority")
    Integer priority;   // Приоритет

    @JsonProperty("instanceId")
    @Transient
    private BigInteger instanceId;  // соответствуеи tpp_product.id (serial) - генерируется автоматом

//    private String registryTypeCode;

    @Transient
    @Size(min = 3, max = 3, message = "Код валюты должен быть трехзначным")
    private String currencyCode;


}
