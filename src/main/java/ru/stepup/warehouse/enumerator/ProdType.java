package ru.stepup.warehouse.enumerator;

public enum ProdType {
    НСО(0), СМО(1),  ЕЖО(2),  ДБДС(3), ДОГОВОР(4);
    int prodTypeId;
    private ProdType(int prodTypeId) {
        this.prodTypeId = prodTypeId;
    }
    public int getProdTypeId() {
        return prodTypeId;
    }
}
