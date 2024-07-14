package ru.stepup.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.stepup.warehouse.entity.ProductRegister;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ProductRegisterRepo extends JpaRepository<ProductRegister, Integer> {

    @Query(value = "select count(*) from tpp_product_register t where t.product_id = ?1 and t.type = ?2", nativeQuery = true)
    int findProductRegistryDouble(BigInteger instanceid, String registryTypeCode);

    @Query(value = "select t.value from tpp_ref_product_register_type t where t.value = ?1", nativeQuery = true)
    List<String> findRegistryTypeCode(String registryTypeCode);

    @Query(value = "SELECT ac.account_number FROM account_pool ap inner join account ac on ac.account_pool_id = ap.id where ap.branch_code = ?1 and ap.currency_code = ?2 and ap.mdm_code = ?3 and ap.priority_code = ?4 and ap.registry_type_code = ?5", nativeQuery = true)
    List<String>  findAccount(String branchCode, String currencyCode, String mdmCode, String priorityCode, String registryTypeCode);


}
