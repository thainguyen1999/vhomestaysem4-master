package com.example.vhomestay.repository;

import com.example.vhomestay.enums.LocalProductPosition;
import com.example.vhomestay.model.dto.response.localproduct.admin.LocalProductDetailForAdminResponse;
import com.example.vhomestay.model.dto.response.localproduct.admin.LocalProductPositionDetail;
import com.example.vhomestay.model.dto.response.localproduct.admin.LocalProductTop5DetailForAdmin;
import com.example.vhomestay.model.dto.response.localproduct.customer.LocalProductDetailForCustomerResponse;
import com.example.vhomestay.model.entity.LocalProduct;
import com.example.vhomestay.model.entity.VillageMedia;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LocalProductRepository extends BaseRepository<LocalProduct, Long>{
    @Query("SELECT lp FROM LocalProduct lp WHERE lp.localProductPosition = :localProductPosition")
    Optional<LocalProduct> findByLocalProductPosition(LocalProductPosition localProductPosition);
    @Query("SELECT new com.example.vhomestay.model.dto.response.localproduct.admin.LocalProductPositionDetail(" +
            "lp.id, lp.productName) " +
            "FROM LocalProduct lp WHERE lp.localProductPosition = null and lp.status = 'ACTIVE'")
    List<LocalProductPositionDetail> findAllLocalProductPosition();
    @Query("SELECT vlm FROM VillageMedia vlm join vlm.localProduct lp WHERE lp.id = :localProductId")
    List<VillageMedia> findAllByLocalProductId(Long localProductId);
    @Query("SELECT new com.example.vhomestay.model.dto.response.localproduct.customer.LocalProductDetailForCustomerResponse(" +
            "lp.id, lp.productName, lp.productDescription, lp.unit, lp.type, lp.lowestPrice, lp.highestPrice)" +
            " FROM LocalProduct lp WHERE lp.status = 'ACTIVE'")
    List<LocalProductDetailForCustomerResponse> findAllByLocalProductForCustomer();
    @Query("SELECT new com.example.vhomestay.model.dto.response.localproduct.customer.LocalProductDetailForCustomerResponse(" +
            "lp.id, lp.productName, lp.productDescription, lp.unit, lp.type, lp.lowestPrice, lp.highestPrice)" +
            " FROM LocalProduct lp WHERE lp.status = 'ACTIVE' AND lp.id = :localProductId")
    Optional<LocalProductDetailForCustomerResponse> findLocalProductByIdForCustomer(Long localProductId);
    @Query("SELECT new com.example.vhomestay.model.dto.response.localproduct.admin.LocalProductDetailForAdminResponse(" +
            "lp.id, lp.productName, lp.productDescription, lp.unit, lp.status, lp.type, lp.lowestPrice, lp.highestPrice)" +
            " FROM LocalProduct lp WHERE lp.status != 'DELETED'")
    List<LocalProductDetailForAdminResponse> findAllByLocalProductForAdmin();
    @Query("SELECT new com.example.vhomestay.model.dto.response.localproduct.admin.LocalProductDetailForAdminResponse(" +
            "lp.id, lp.productName, lp.productDescription, lp.unit, lp.status, lp.type, lp.lowestPrice, lp.highestPrice)" +
            " FROM LocalProduct lp WHERE lp.status != 'DELETED' AND lp.id = :localProductId")
    Optional<LocalProductDetailForAdminResponse> findLocalProductByIdForAdmin(Long localProductId);
    @Query("SELECT new com.example.vhomestay.model.dto.response.localproduct.admin.LocalProductTop5DetailForAdmin(" +
            "lp.localProductPosition, lp.id, lp.productName, lp.type) " +
            "FROM LocalProduct lp WHERE lp.status = 'ACTIVE' and lp.localProductPosition != null ORDER BY lp.localProductPosition ASC")
    List<LocalProductTop5DetailForAdmin> getTop5LocalProduct();
    @Query("SELECT count(lp) FROM LocalProduct lp WHERE lp.status != 'DELETED'")
    Integer countAllLocalProduct();
    @Query("SELECT lp FROM LocalProduct lp WHERE lp.status = 'ACTIVE' and lp.localProductPosition != NULL ORDER BY lp.localProductPosition ASC")
    List<LocalProduct> getLocalProductTOP5InHomePage();
}
