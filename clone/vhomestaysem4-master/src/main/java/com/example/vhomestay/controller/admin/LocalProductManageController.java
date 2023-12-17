package com.example.vhomestay.controller.admin;

import com.example.vhomestay.model.dto.request.localproduct.LocalProductDetailForEditRequest;
import com.example.vhomestay.model.dto.request.localproduct.LocalProductPositionRequest;
import com.example.vhomestay.model.dto.response.MessageResponseDto;
import com.example.vhomestay.model.dto.response.localproduct.admin.*;
import com.example.vhomestay.service.LocalProductService;
import com.example.vhomestay.service.impl.LocalProductServiceImpl;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/local-product")
@RequiredArgsConstructor
public class LocalProductManageController {

    private final LocalProductService localProductService;
    private final MessageSource messageSource;

    @GetMapping
    ListLocalProductForAdmin getAllLocalProductForAdmin() {
        List<LocalProductDetailForAdminResponse> localProductListForAdmin = localProductService.findAllLocalProductForAdmin();
        return ListLocalProductForAdmin.builder().localProductListForAdmin(localProductListForAdmin).build();
    }

    @GetMapping("/{localProductId}")
    LocalProductDetailForAdminResponse getLocalProductByIdForAdmin(@PathVariable Long localProductId) {
        return localProductService.findLocalProductByIdForAdmin(localProductId);
    }

    @GetMapping("/add")
    ResponseEntity<?> showAddLocalProductForm() {
        List<String> localProductTypeList = localProductService.showAddLocalProductForm();
        Map<String, Object> response = Map.of("localProductTypeList", localProductTypeList);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    ResponseEntity<?> addLocalProduct(@ModelAttribute LocalProductDetailForEditRequest localProductDetailForEditRequest) throws IOException {
        if (localProductService.addLocalProduct(localProductDetailForEditRequest)){
            return ResponseEntity.ok("local.product.create.success");
        } else {
            throw new ResourceBadRequestException("local.product.add.failed");
        }
    }

    @GetMapping("/edit/{localProductId}")
    EditLocalProductForm showEditLocalProductForm(@PathVariable Long localProductId) {
        return localProductService.showEditLocalProductForm(localProductId);
    }

    @PutMapping("/edit")
    ResponseEntity<?> editLocalProduct(@ModelAttribute LocalProductDetailForEditRequest localProductDetailForEditRequest) throws IOException {
        if (localProductService.editLocalProduct(localProductDetailForEditRequest)){
            return ResponseEntity.ok("local.product.update.success");
        } else {
            throw new ResourceBadRequestException("local.product.update.failed");
        }
    }

    @GetMapping("/top")
    ResponseEntity<?> getTop5LocalProduct() {
        List<LocalProductTop5DetailForAdmin> localProductTop5DetailForAdmins = localProductService.getTop5LocalProduct();
        AddLocalProductResponse addLocalProductResponse = localProductService.findAllLocalProductHaveNoPosition();
        Map<String, Object> response = Map.of(
                "localProductTop5DetailForAdmins", localProductTop5DetailForAdmins,
                "localProductListForAdmin", addLocalProductResponse.getLocalProductPositionListForAdmin(),
                "localProductPositionList", addLocalProductResponse.getLocalProductNullPositionListForAdmin()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/set-position")
    public ResponseEntity<?> setLocalProductPosition(@RequestBody LocalProductPositionRequest localProductPositionRequest) {
        if (localProductService.setLocalProductPosition(localProductPositionRequest.getLocalProductId(), localProductPositionRequest.getLocalProductPosition())){
            return ResponseEntity.ok("local.product.position.set.success");
        } else {
            throw new ResourceBadRequestException("local.product.not.found");
        }
    }

    @PutMapping("/delete-position")
    public ResponseEntity<?> deleteLocalProductPosition(@RequestBody LocalProductPositionRequest localProductPositionRequest) {
        if (localProductService.deleteLocalProductPosition(localProductPositionRequest.getLocalProductId(), localProductPositionRequest.getLocalProductPosition())){
            return ResponseEntity.ok("local.product.position.delete.success");
        } else {
            throw new ResourceBadRequestException("local.product.position.delete.failed");
        }
    }

    @PutMapping("/{localProductId}/active")
    public ResponseEntity<?> activateLocalProduct(@PathVariable Long localProductId) {
        if (localProductService.activateLocalProduct(localProductId)){
            return ResponseEntity.ok("local.product.active.success");
        } else {
            throw new ResourceBadRequestException("local.product.not.found");
        }
    }

    @PutMapping("/{localProductId}/inactive")
    public ResponseEntity<?> inactivateLocalProduct(@PathVariable Long localProductId) {
        if (localProductService.inactivateLocalProduct(localProductId)){
            MessageResponseDto messageResponseDto = new MessageResponseDto("Inactivate local product successfully.", HttpStatus.OK);
            return ResponseEntity.ok(messageResponseDto);
        } else {
            throw new ResourceBadRequestException("local.product.inactive.success");
        }
    }

    @PutMapping("/{localProductId}/delete")
    public ResponseEntity<?> deleteLocalProduct(@PathVariable Long localProductId) {
        if (localProductService.deleteLocalProduct(localProductId)){
            return ResponseEntity.ok("local.product.delete.success");
        } else {
            throw new ResourceBadRequestException("local.product.not.found");
        }
    }
}
