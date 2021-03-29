package controller;

import model.Product;
import model.ProductFrom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import service.IProductService;
import service.ProductService;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("product")
@PropertySource("classpath:upload_file.properties")
public class ProductController {
    private final IProductService productService = new ProductService();
    @Value("${file-upload}")
    private String fileUpload;

    @GetMapping("")
    public String index(Model model) {
        List<Product> products = productService.finAll();
        model.addAttribute("products", products);
        return "/index";
    }

    @GetMapping("/create")
    public ModelAndView showCreate() {
        ModelAndView modelAndView = new ModelAndView("/create");
        modelAndView.addObject("productForm", new ProductFrom());
        return modelAndView;
    }

    @PostMapping("/save")
    public ModelAndView saveProduct(@ModelAttribute ProductFrom productFrom) {
        MultipartFile multipartFile = productFrom.getImage();
        String fileName = multipartFile.getOriginalFilename();
        try {
            FileCopyUtils.copy(productFrom.getImage().getBytes(), new File(fileUpload + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Product product = new Product(productFrom.getId(), productFrom.getName(), productFrom.getDescription(), fileName);
        productService.save(product);
        ModelAndView modelAndView = new ModelAndView("/create");
        modelAndView.addObject("productForm", productFrom);
        modelAndView.addObject("message", "Created new product successfully !");
        return modelAndView;
    }
}
