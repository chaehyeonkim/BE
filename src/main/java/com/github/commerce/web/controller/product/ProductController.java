package com.github.commerce.web.controller.product;

import com.github.commerce.entity.Product;
import com.github.commerce.entity.User;
import com.github.commerce.entity.collection.ProductOption;
import com.github.commerce.repository.user.UserDetailsImpl;
import com.github.commerce.service.product.ProductService;
import com.github.commerce.web.dto.product.ProductDto;
import com.github.commerce.web.dto.product.ProductRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/api/product")
@Api(tags = "상품 CRUD API")
public class ProductController {

    private final ProductService productService;
    @ApiOperation(value = "상품 검색")
    @GetMapping("/search") //  ?pageNumber=1&searchWord=반바지
    public ResponseEntity<List<ProductDto>> searchProduct(
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "searchWord", required = false) String searchWord,
            @RequestParam(name = "sortBy", required = false, defaultValue = "price") String sortBy
    ){
        return ResponseEntity.ok(productService.getProducts(pageNumber, searchWord, sortBy));
    }
    @ApiOperation(value = "상품 상세 조회")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(
            @PathVariable Long productId
    ){
        return ResponseEntity.ok(productService.getOneProduct(productId));
    }
    @ApiOperation(value = "카테고리 검색")
    @GetMapping("/category")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(
            @RequestParam(name = "productCategory", required = false, defaultValue = "상의") String productCategory,
            @RequestParam(name = "ageCategory", required = false, defaultValue = "이십대") String ageCategory,
            @RequestParam(name = "genderCategory", required = false, defaultValue = "female") String genderCategory,
            @RequestParam(name = "sortBy", required = false, defaultValue = "price") String sortBy
            )
    {
        return ResponseEntity.ok(productService.getProductsByCategory(productCategory,ageCategory,genderCategory, sortBy));
    }


    // 판매자가 상품 등록
    @ApiOperation(value = "상품 등록")
    @PostMapping
    public ResponseEntity<String> createProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @ModelAttribute(value="productRequest") ProductRequest productRequest,
                                           @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        Long profileId = userDetails.getUser().getId();
        System.out.printf("1111111" + productRequest.getName());

        return ResponseEntity.ok(productService.createProductItem(productRequest, imageFiles, profileId));
    }

    // 판매자가 상품 조회 -> 날짜별 올린 상품과 판매 완료 상품 조회 가능 하도록

    // 구매자가 구매 상품 조회

    // 상품 검색 (카테고리 검색)

    // 상품 수정
    @ApiOperation(value = "상품 식별값을 입력하여 단일의 product 레코드를 수정합니다.")
    @PatchMapping(value = "/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable("productId") Long productId,
                                           @ModelAttribute ProductRequest productRequest,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails
                                           ) {
        Long profileId = userDetails.getUser().getId();
        productService.updateProductById(productId,profileId,productRequest);

        return ResponseEntity.ok(profileId + "번 상품 수정 성공");
    }



    // 상품 삭제
    @ApiOperation(value="상품 식별값을 입력하여 단일의 product 레코드를 삭제합니다.")
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("productId") Long productId){
        Long profileId = userDetails.getUser().getId();
        productService.deleteProductByProductId(productId,profileId);
        return ResponseEntity.ok(profileId + "번 상품 삭제 성공");
    }

}
