package ru.stepup.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.stepup.warehouse.enumerator.Status;

import java.math.BigInteger;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("data")   // используем для создания узла json - "data":
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME) // используем для создания узла json - "data":
@Table(name = "tpp_product_register")
public class ProductRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="product_id")
    @NotNull(message = "поле instanceid не может быть пустым")
    private BigInteger instanceid;
    @Column(name="type", nullable = false)
    private String registryTypeCode;
    @Size(min = 3, max = 3, message = "Код валюты должен быть трехзначным")
    @Column(name="currency_code")
    private String currencyCode;
    @Enumerated(EnumType.STRING)
    private Status state;
    private BigInteger account;
    private String account_number;

    @Transient
    private String accountType; // Тип счета
    @Transient
    private String branchCode; // Код филиала
    @Transient
    private String priorityCode; // Код срочности
    @Transient
    private String mdmCode; // Id Клиента
    @Transient
    private String trainRegion;
    @Transient
    private String counter;
    @Transient
    private String salesCode;

    @Override
    public String toString() {
        return "ProductRegister{" +
                "id=" + id +
                ", product_id=" + instanceid +
                ", type='" + registryTypeCode + '\'' +
                ", currency_code='" + currencyCode + '\'' +
                ", state='" + state.getStatusId() + '\'' +
                ", account='" + account + '\'' +
                ", account_number='" + account_number + '\'' +
                '}';
    }
}
