package com.example.vhomestay.service.impl;

import com.example.vhomestay.enums.BaseStatus;
import com.example.vhomestay.enums.LocalProductPosition;
import com.example.vhomestay.enums.LocalProductType;
import com.example.vhomestay.enums.MediaType;
import com.example.vhomestay.model.dto.request.localproduct.LocalProductDetailForEditRequest;
import com.example.vhomestay.model.dto.response.localproduct.admin.*;
import com.example.vhomestay.model.dto.response.localproduct.customer.LocalProductDetailForCustomerResponse;
import com.example.vhomestay.model.entity.LocalProduct;
import com.example.vhomestay.model.entity.VillageMedia;
import com.example.vhomestay.repository.LocalProductRepository;
import com.example.vhomestay.repository.VillageMediaRepository;
import com.example.vhomestay.service.LocalProductService;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocalProductServiceImpl extends BaseServiceImpl<LocalProduct, Long, LocalProductRepository>
        implements LocalProductService  {

    private final LocalProductRepository localProductRepository;
    private final StorageServiceImpl storageService;
    private final VillageMediaRepository villageMediaRepository;

    @Override
    public List<LocalProductDetailForCustomerResponse> findAllLocalProductForCustomer() {
        List<LocalProductDetailForCustomerResponse> localProductListForCustomer = localProductRepository.findAllByLocalProductForCustomer();
        for (LocalProductDetailForCustomerResponse localProductDetailForCustomerResponse : localProductListForCustomer) {
            localProductDetailForCustomerResponse.setVillageMedias(localProductRepository.findAllByLocalProductId(localProductDetailForCustomerResponse.getId()));
        }
        return localProductListForCustomer;
    }

    @Override
    public LocalProductDetailForCustomerResponse findLocalProductByIdForCustomer(Long localProductId) {
        Optional<LocalProductDetailForCustomerResponse> optionalLocalProductDetailForCustomerResponse = localProductRepository.findLocalProductByIdForCustomer(localProductId);
        if (optionalLocalProductDetailForCustomerResponse.isEmpty()){
            throw new ResourceBadRequestException("local.product.not.found");
        }
        optionalLocalProductDetailForCustomerResponse.get().setVillageMedias(localProductRepository.findAllByLocalProductId(localProductId));
        return optionalLocalProductDetailForCustomerResponse.get();
    }


    @Override
    public List<LocalProductDetailForAdminResponse> findAllLocalProductForAdmin() {
        List<LocalProductDetailForAdminResponse> localProductListForAdmin = localProductRepository.findAllByLocalProductForAdmin();
        int size = localProductListForAdmin.size();
        int i;
        for (i=0; i<size; i++) {
            localProductListForAdmin.get(i).setVillageMedias(localProductRepository.findAllByLocalProductId(localProductListForAdmin.get(i).getId()));
        }
        return localProductListForAdmin;
    }

    @Override
    public LocalProductDetailForAdminResponse findLocalProductByIdForAdmin(Long localProductId) {
        Optional<LocalProductDetailForAdminResponse> optionalLocalProductDetailForAdminResponse = localProductRepository.findLocalProductByIdForAdmin(localProductId);
        if (optionalLocalProductDetailForAdminResponse.isEmpty()){
            throw new ResourceBadRequestException("local.product.not.found");
        }
        optionalLocalProductDetailForAdminResponse.get().setVillageMedias(localProductRepository.findAllByLocalProductId(localProductId));
        return optionalLocalProductDetailForAdminResponse.get();
    }

    @Override
    public List<LocalProductTop5DetailForAdmin> getTop5LocalProduct() {
        return localProductRepository.getTop5LocalProduct();
    }

    @Override
    public AddLocalProductResponse findAllLocalProductHaveNoPosition() {

        AddLocalProductResponse addLocalProductResponse = new AddLocalProductResponse();

        List<String> localProductNullPosition = new ArrayList<>();
        List<LocalProductPositionDetail> localProductPositionDetailList = localProductRepository.findAllLocalProductPosition();
        List<LocalProductTop5DetailForAdmin> localProductTop5DetailForAdminList = localProductRepository.getTop5LocalProduct();

        List<LocalProductPosition> localProductPositions = Arrays.asList(LocalProductPosition.values());
        for (LocalProductPosition localProductPosition : localProductPositions){
            if (localProductTop5DetailForAdminList.stream().noneMatch(obj
                    -> obj.getLocalProductPosition().equals(localProductPosition))){
                localProductNullPosition.add(LocalProductPosition.valueOf(localProductPosition.toString()).toString());
            }
        }

        addLocalProductResponse.setLocalProductPositionListForAdmin(localProductPositionDetailList);
        addLocalProductResponse.setLocalProductNullPositionListForAdmin(localProductNullPosition);

        return addLocalProductResponse;
    }

    @Override
    public List<String> showAddLocalProductForm() {
        return Arrays.stream(LocalProductType.values())
                .map(LocalProductType::toString)
                .collect(Collectors.toList());
    }

    @Override
    public boolean addLocalProduct(LocalProductDetailForEditRequest localProductDetailForAddRequest) throws IOException {

        LocalProduct localProduct = new LocalProduct();

        localProduct.setProductName(localProductDetailForAddRequest.getLocalProductName());
        localProduct.setType(localProductDetailForAddRequest.getLocalProductType());
        localProduct.setProductDescription(localProductDetailForAddRequest.getLocalProductDescription());
        localProduct.setLowestPrice(localProductDetailForAddRequest.getLocalProductMinPrice());
        localProduct.setHighestPrice(localProductDetailForAddRequest.getLocalProductMaxPrice());
        localProduct.setUnit(localProductDetailForAddRequest.getLocalProductUnit());
        localProduct.setStatus(BaseStatus.ACTIVE);
        localProductRepository.save(localProduct);
        List<MultipartFile> images = localProductDetailForAddRequest.getImagesFile();
        if (images == null) {
            return true;
        }
        MultipartFile[] imagesArray = new MultipartFile[images.size()];
        imagesArray = images.toArray(imagesArray);
        addImagesToVillageMedia(localProduct, imagesArray);
        return true;
    }

    @Override
    public EditLocalProductForm showEditLocalProductForm(Long localProductId) {
        Optional<LocalProduct> optionalLocalProduct = localProductRepository.findById(localProductId);
        if (optionalLocalProduct.isEmpty()){
            throw new ResourceNotFoundException("local.product.not.found");
        }
        LocalProduct localProduct = optionalLocalProduct.get();
        return EditLocalProductForm.builder()
                .localProductId(localProduct.getId())
                .localProductName(localProduct.getProductName())
                .localProductType(localProduct.getType())
                .localProductDescription(localProduct.getProductDescription())
                .localProductMinPrice(localProduct.getLowestPrice())
                .localProductMaxPrice(localProduct.getHighestPrice())
                .localProductTypes(Arrays.stream(LocalProductType.values())
                        .map(LocalProductType::toString)
                        .collect(Collectors.toList()))
                .villageMedias(localProduct.getVillageMedias())
                .localProductUnit(localProduct.getUnit())
                .build();
    }

    @Override
    public boolean editLocalProduct(LocalProductDetailForEditRequest localProductDetailForEditRequest) throws IOException {
        LocalProduct localProduct = localProductRepository.findById(localProductDetailForEditRequest.getLocalProductId()).orElse(null);
        if (localProduct == null) {
            return false;
        }
        localProduct.setProductName(localProductDetailForEditRequest.getLocalProductName());
        localProduct.setType(localProductDetailForEditRequest.getLocalProductType());
        localProduct.setProductDescription(localProductDetailForEditRequest.getLocalProductDescription());
        localProduct.setLowestPrice(localProductDetailForEditRequest.getLocalProductMinPrice());
        localProduct.setHighestPrice(localProductDetailForEditRequest.getLocalProductMaxPrice());
        localProduct.setUnit(localProductDetailForEditRequest.getLocalProductUnit());

        List<VillageMedia> oldVillageMediaListToDelete = villageMediaRepository.findAllByLocalProductId(localProduct.getId());
        List<VillageMedia> newVillageMediaList = localProductDetailForEditRequest.getVillageMedias();
        oldVillageMediaListToDelete.removeIf(newVillageMediaList::contains);

        if (!oldVillageMediaListToDelete.isEmpty()) {
            for (VillageMedia media : oldVillageMediaListToDelete) {
                storageService.deleteFile(media.getFilePath());
                villageMediaRepository.delete(media);
            }
        }

        for (VillageMedia media : oldVillageMediaListToDelete) {
            storageService.deleteFile(media.getFilePath());
            villageMediaRepository.delete(media);
        }

        if (localProductDetailForEditRequest.getImagesFile() != null && !localProductDetailForEditRequest.getImagesFile().isEmpty()) {
            for (int i = 0; i < localProductDetailForEditRequest.getImagesFile().size(); i++) {
                createVillageMedia(localProduct, localProductDetailForEditRequest.getImagesFile().get(i).getOriginalFilename(),
                        storageService.uploadFile(localProductDetailForEditRequest.getImagesFile().get(i)));
            }
        }

        localProduct.setVillageMedias(villageMediaRepository.findAllByLocalProductId(localProduct.getId()));
        localProductRepository.save(localProduct);
        return true;
    }

    private void createVillageMedia(LocalProduct localProduct, String fileName, String filePath) {
        if (!Objects.equals(fileName, "")) {
            VillageMedia villageMedia = new VillageMedia();
            villageMedia.setLocalProduct(localProduct);
            villageMedia.setFileName(fileName);
            villageMedia.setFilePath(filePath);
            villageMedia.setType(MediaType.IMAGE);
            villageMediaRepository.save(villageMedia);
        }
    }

    @Override
    public void addImagesToVillageMedia(LocalProduct localProduct, MultipartFile[] images) throws IOException {
        for (MultipartFile image : images) {
            String imageName = image.getOriginalFilename();
            String imageUrl = storageService.uploadFile(image);
            saveImageToDatabase(localProduct, imageName, imageUrl);
        }
    }

    private void saveImageToDatabase(LocalProduct localProduct, String imageName, String imageUrl) {
        VillageMedia villageMedia = new VillageMedia();
        villageMedia.setLocalProduct(localProduct);
        villageMedia.setFileName(imageName);
        villageMedia.setFilePath(imageUrl);
        villageMedia.setType(MediaType.IMAGE);
        villageMediaRepository.save(villageMedia);
    }

    @Override
    public boolean setLocalProductPosition(Long localProductId, LocalProductPosition localProductPosition) {
        Optional<LocalProduct> optionalLocalProductOld = localProductRepository.findByLocalProductPosition(localProductPosition);
        if (optionalLocalProductOld.isPresent()) {
            optionalLocalProductOld.get().setLocalProductPosition(null);
            localProductRepository.save(optionalLocalProductOld.get());
        }
        Optional<LocalProduct> optionalLocalProduct = localProductRepository.findById(localProductId);
        if (optionalLocalProduct.isPresent()) {
            optionalLocalProduct.get().setLocalProductPosition(localProductPosition);
            localProductRepository.save(optionalLocalProduct.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteLocalProductPosition(Long localProductId, LocalProductPosition localProductPosition) {
        Optional<LocalProduct> optionalLocalProduct = localProductRepository.findById(localProductId);
        Optional<LocalProduct> optionalLocalProductOld = localProductRepository.findByLocalProductPosition(localProductPosition);
        if (optionalLocalProduct.isPresent() && Objects.equals(optionalLocalProduct.get().getId(), optionalLocalProductOld.get().getId())) {
            optionalLocalProduct.get().setLocalProductPosition(null);
            localProductRepository.save(optionalLocalProduct.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean activateLocalProduct(Long localProductId) {
        Optional<LocalProduct> localProduct = localProductRepository.findById(localProductId);
        if (localProduct.isPresent()) {
            localProduct.get().setStatus(BaseStatus.ACTIVE);
            localProductRepository.save(localProduct.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean inactivateLocalProduct(Long localProductId) {
        Optional<LocalProduct> localProduct = localProductRepository.findById(localProductId);
        if (localProduct.isPresent()) {
            localProduct.get().setStatus(BaseStatus.INACTIVE);
            localProductRepository.save(localProduct.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteLocalProduct(Long localProductId) {
        Optional<LocalProduct> localProduct = localProductRepository.findById(localProductId);
        if (localProduct.isPresent()) {
            localProduct.get().setStatus(BaseStatus.DELETED);
            localProductRepository.save(localProduct.get());
            return true;
        }
        return false;
    }

    @Override
    public List<LocalProduct> getLocalProductTOP5InHomePage() {
        return localProductRepository.getLocalProductTOP5InHomePage();
    }
}
