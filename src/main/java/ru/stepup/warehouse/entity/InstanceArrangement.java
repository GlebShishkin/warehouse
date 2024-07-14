package ru.stepup.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.stepup.warehouse.enumerator.ProdType;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "agreement")
public class InstanceArrangement implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="product_id")
    Integer product_id;

    @JsonProperty("GeneralAgreementId")
    @Column(name="general_agreement_id")
    String GeneralAgreementId;  // ID доп.Ген.соглашения

    @JsonProperty("SupplementaryAgreementId")
    @Column(name="supplementary_agreement_id")
    String SupplementaryAgreementId;    // ID доп.соглашения

    @Enumerated(EnumType.STRING)    // значение будет сохранено в базу как строка
    @JsonProperty("arrangementType")
    @Column(name="arrangement_type")
    ProdType arrangementType;   // Тип соглашения

    @JsonProperty("Number")
    @Column(name="number")
    private String number;  // Номер ДС

    @JsonProperty("openingDate")
    @Column(name="opening_date")
    private Date openingDate;   // Дата начала сделки

    @JsonProperty("cancellation_reason")
    @Column(name="cancellation_reason")
    private String cancellationReason;  // Причина расторжения

    @Override
    public String toString() {
        return "InstanceArrangement{" +
                "id=" + id +
                ", Number='" + number + '\'' +
                ", openingDate=" + openingDate + '\'' +
                ", cancellationReason=" + cancellationReason +
                '}';
    }
}
