package com.penglecode.codeforce.mybatismds.examples.test;

import com.penglecode.codeforce.common.util.JsonUtils;
import com.penglecode.codeforce.mybatismds.examples.GmdsExample1Application;
import com.penglecode.codeforce.mybatismds.examples.product.domain.enums.ProductTypeEnum;
import com.penglecode.codeforce.mybatismds.examples.product.domain.model.Product;
import com.penglecode.codeforce.mybatismds.examples.product.domain.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 商品模块的Mapper测试
 *
 * @author pengpeng
 * @since 2.1
 */
@SpringBootTest(classes=GmdsExample1Application.class)
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    public void createProducts() {
        createProduct(10047932928981L, "华为p50pro手机 曜金黑 8G+256G 麒麟版", "https://item.jd.com/10047932928981.html", 718800L, 6);
        createProduct(10028910257756L, "华为 HUAWEI Mate 40 Pro 麒麟9000芯片 超感知徕卡影像 4G｜5G手机 亮黑色 8+128G", "https://item.jd.com/10028910257756.html", 598800L, 4);
        createProduct(100014352499L, "Apple iPad 10.2英寸平板电脑 2021年款（64GB WLAN版/A13芯片/1200万像素/iPadOS MK2K3CH/A） 深空灰色", "https://item.jd.com/100014352499.html", 249900L, 12);
        createProduct(100028056874L, "Apple MacBook Pro 16英寸M1 Pro芯片(10核中央处理器 16核图形处理器) 16G 512G深空灰笔记本电脑MK183CH/A", "https://item.jd.com/100028056874.html", 1899900L, 3);
        createProduct(10035789319416L, "【华强北4代顶配版】Air3苹果蓝牙耳机双耳无线降噪适用iphone 13/12/11入耳四代维肯 旗舰全功能pods3【4月空间音频降噪版】 【秒弹窗+指纹触控+改名定位+蓝牙5.2】", "https://item.jd.com/10035789319416.html", 22900L, 5);
    }

    private void createProduct(Long productId, String productName, String productUrl, Long unitPrice, Integer inventory) {
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName(productName);
        product.setProductUrl(productUrl);
        product.setProductType(ProductTypeEnum.PHYSICAL_PRODUCT.getTypeCode());
        product.setUnitPrice(unitPrice);
        product.setInventory(inventory);
        productService.createProduct(product);
    }

    @Test
    public void getProductById() {
        Product product = productService.getProductById(10035789319416L);
        System.out.println(JsonUtils.object2Json(product));
    }

    @Test
    public void incrProductInventory() {
        productService.incrProductInventory(10035789319416L, 5);
    }

    @Test
    public void decrProductInventory() {
        productService.decrProductInventory1(10035789319416L, 5);
    }

}
