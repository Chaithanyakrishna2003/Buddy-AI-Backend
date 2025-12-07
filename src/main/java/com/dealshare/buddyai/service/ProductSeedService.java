package com.dealshare.buddyai.service;

import com.dealshare.buddyai.model.Product;
import com.dealshare.buddyai.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSeedService {

    private final ProductRepository productRepository;
    private final Random random = new Random();

    @Bean
    public CommandLineRunner seedProducts() {
        return args -> {
            long existingCount = productRepository.count();
            log.info("Current product count: {}", existingCount);
            
            // Always update existing products' image URLs to match product names
            updateExistingProductImages();
            
            if (existingCount < 200) {
                int productsToAdd = 200 - (int) existingCount;
                log.info("Seeding {} diverse products...", productsToAdd);
                
                // Get max product ID to continue from
                int maxId = productRepository.findAll().stream()
                        .mapToInt(Product::getProductId)
                        .max()
                        .orElse(999);
                
                List<Product> products = generateDiverseProducts(maxId + 1);
                
                // Filter out duplicates before saving
                List<Product> newProducts = filterDuplicates(products);
                
                if (!newProducts.isEmpty()) {
                    productRepository.saveAll(newProducts);
                    log.info("Successfully seeded {} new products ({} duplicates skipped). Total products: {}", 
                            newProducts.size(), products.size() - newProducts.size(), productRepository.count());
                } else {
                    log.info("All products already exist in database. No new products added.");
                }
            } else {
                log.info("Sufficient products already exist in database ({}). Skipping seed.", existingCount);
            }
        };
    }
    
    private void updateExistingProductImages() {
        try {
            List<Product> allProducts = productRepository.findAll();
            int updatedCount = 0;
            
            log.info("Updating image URLs for {} products to use Wikimedia Commons...", allProducts.size());
            
            for (Product product : allProducts) {
                String newImageUrl = generateImageUrl(product.getProductName(), product.getCategory());
                // Always update to ensure Wikimedia Commons URLs are used
                product.setImageUrl(newImageUrl);
                product.setUpdatedAt(LocalDateTime.now());
                productRepository.save(product);
                updatedCount++;
            }
            
            log.info("✅ Updated image URLs for all {} products to use Wikimedia Commons images", updatedCount);
        } catch (Exception e) {
            log.error("Error updating product images: {}", e.getMessage(), e);
        }
    }
    
    private List<Product> filterDuplicates(List<Product> products) {
        List<Product> newProducts = new ArrayList<>();
        int duplicateCount = 0;
        
        for (Product product : products) {
            // Check if product with same name exists (case insensitive)
            List<Product> existingByName = productRepository.findByProductNameIgnoreCase(product.getProductName());
            
            // Check if product with same SKU exists
            Product existingBySku = productRepository.findBySkuCode(product.getSkuCode());
            
            if (existingByName.isEmpty() && existingBySku == null) {
                newProducts.add(product);
            } else {
                duplicateCount++;
                if (existingByName.isEmpty()) {
                    log.debug("Skipping duplicate product: SKU {} already exists", product.getSkuCode());
                } else {
                    log.debug("Skipping duplicate product: '{}' already exists", product.getProductName());
                }
            }
        }
        
        if (duplicateCount > 0) {
            log.info("Found {} duplicate products, skipping them", duplicateCount);
        }
        
        return newProducts;
    }

    private List<Product> generateDiverseProducts(int startId) {
        List<Product> products = new ArrayList<>();
        int productId = startId;

        // Fruits & Vegetables (40 products)
        products.addAll(generateFruitsVegetables(productId));
        productId += 40;

        // Grains & Pulses (30 products)
        products.addAll(generateGrainsPulses(productId));
        productId += 30;

        // Spices & Condiments (25 products)
        products.addAll(generateSpicesCondiments(productId));
        productId += 25;

        // Dairy Products (20 products)
        products.addAll(generateDairyProducts(productId));
        productId += 20;

        // Beverages (20 products)
        products.addAll(generateBeverages(productId));
        productId += 20;

        // Snacks & Sweets (25 products)
        products.addAll(generateSnacksSweets(productId));
        productId += 25;

        // Personal Care (20 products)
        products.addAll(generatePersonalCare(productId));
        productId += 20;

        // Household & Cleaning (20 products)
        products.addAll(generateHouseholdCleaning(productId));

        return products;
    }

    private List<Product> generateFruitsVegetables(int startId) {
        String[] items = {
            "Fresh Tomatoes", "Potatoes", "Onions", "Carrots", "Capsicum",
            "Cauliflower", "Cabbage", "Brinjal", "Okra", "Bottle Gourd",
            "Cucumber", "Radish", "Beetroot", "Spinach", "Coriander Leaves",
            "Mint Leaves", "Curry Leaves", "Green Chilies", "Ginger", "Garlic",
            "Apples", "Bananas", "Oranges", "Grapes", "Pomegranate",
            "Mangoes", "Watermelon", "Muskmelon", "Papaya", "Guava",
            "Pineapple", "Strawberries", "Kiwi", "Sweet Lime", "Custard Apple",
            "Dragon Fruit", "Avocado", "Pear", "Plum", "Cherry"
        };
        
        String[] brands = {"Fresh Farm", "Organic Valley", "Nature's Best", "Farm Fresh", "Green Harvest"};
        return generateProducts(startId, items, brands, "Fruits & Vegetables", 20, 500, 0.1, 0.3);
    }

    private List<Product> generateGrainsPulses(int startId) {
        String[] items = {
            "Basmati Rice", "Sona Masoori Rice", "Brown Rice", "Jasmine Rice", "Red Rice",
            "Wheat Flour", "Gram Flour", "Ragi Flour", "Bajra Flour", "Jowar Flour",
            "Toor Dal", "Moong Dal", "Chana Dal", "Urad Dal", "Masoor Dal",
            "Rajma", "Black Gram", "Green Gram", "Horse Gram", "Cowpea",
            "Quinoa", "Barley", "Oats", "Millet", "Buckwheat",
            "Semolina", "Couscous", "Bulgur", "Pearl Barley", "Split Peas"
        };
        
        String[] brands = {"India Gate", "Kohinoor", "Tata", "Fortune", "Aashirvaad"};
        return generateProducts(startId, items, brands, "Grains & Pulses", 50, 800, 0.15, 0.25);
    }

    private List<Product> generateSpicesCondiments(int startId) {
        String[] items = {
            "Turmeric Powder", "Red Chili Powder", "Coriander Powder", "Cumin Powder", "Garam Masala",
            "Black Pepper", "Cumin Seeds", "Mustard Seeds", "Fenugreek Seeds", "Fennel Seeds",
            "Cardamom", "Cinnamon", "Cloves", "Bay Leaves", "Star Anise",
            "Saffron", "Asafoetida", "Tamarind", "Curry Powder", "Biryani Masala",
            "Sambar Powder", "Rasam Powder", "Chaat Masala", "Pav Bhaji Masala", "Tandoori Masala"
        };
        
        String[] brands = {"Everest", "MDH", "Catch", "Aachi", "MTR"};
        return generateProducts(startId, items, brands, "Spices & Condiments", 30, 500, 0.2, 0.4);
    }

    private List<Product> generateDairyProducts(int startId) {
        String[] items = {
            "Full Cream Milk", "Toned Milk", "Skimmed Milk", "Curd", "Paneer",
            "Butter", "Ghee", "Cheese", "Cream", "Buttermilk",
            "Yogurt", "Greek Yogurt", "Cottage Cheese", "Mozzarella", "Cheddar Cheese",
            "Ice Cream Vanilla", "Ice Cream Chocolate", "Ice Cream Strawberry", "Frozen Yogurt", "Kulfi"
        };
        
        String[] brands = {"Amul", "Mother Dairy", "Arokya", "Heritage", "Nestle"};
        return generateProducts(startId, items, brands, "Dairy Products", 40, 300, 0.1, 0.2);
    }

    private List<Product> generateBeverages(int startId) {
        String[] items = {
            "Green Tea", "Black Tea", "Masala Chai", "Coffee Powder", "Instant Coffee",
            "Orange Juice", "Apple Juice", "Mango Juice", "Mixed Fruit Juice", "Coconut Water",
            "Lemonade", "Buttermilk", "Lassi", "Smoothie Mix", "Energy Drink",
            "Soft Drink Cola", "Soft Drink Orange", "Soft Drink Lemon", "Mineral Water", "Sparkling Water"
        };
        
        String[] brands = {"Tata Tea", "Red Label", "Bru", "Nescafe", "Real"};
        return generateProducts(startId, items, brands, "Beverages", 25, 200, 0.15, 0.3);
    }

    private List<Product> generateSnacksSweets(int startId) {
        String[] items = {
            "Potato Chips", "Kurkure", "Namkeen", "Mixture", "Bhujia",
            "Biscuits", "Cookies", "Cream Biscuits", "Glucose Biscuits", "Digestive Biscuits",
            "Chocolate Bar", "Dark Chocolate", "White Chocolate", "Candies", "Toffees",
            "Gulab Jamun", "Rasgulla", "Barfi", "Ladoo", "Jalebi",
            "Cake", "Pastry", "Donuts", "Muffins", "Brownies"
        };
        
        String[] brands = {"Lay's", "Haldiram", "Parle", "Britannia", "Cadbury"};
        return generateProducts(startId, items, brands, "Snacks & Sweets", 20, 300, 0.2, 0.35);
    }

    private List<Product> generatePersonalCare(int startId) {
        String[] items = {
            "Soap", "Body Wash", "Shampoo", "Conditioner", "Face Wash",
            "Toothpaste", "Toothbrush", "Mouthwash", "Hand Sanitizer", "Hand Soap",
            "Deodorant", "Perfume", "Body Lotion", "Face Cream", "Sunscreen",
            "Hair Oil", "Shaving Cream", "Razor", "Cotton Swabs", "Tissue Paper"
        };
        
        String[] brands = {"Dove", "Ponds", "Lakme", "Himalaya", "Colgate"};
        return generateProducts(startId, items, brands, "Personal Care", 50, 500, 0.15, 0.3);
    }

    private List<Product> generateHouseholdCleaning(int startId) {
        String[] items = {
            "Detergent Powder", "Detergent Liquid", "Dish Soap", "Floor Cleaner", "Toilet Cleaner",
            "Glass Cleaner", "Furniture Polish", "Air Freshener", "Mosquito Repellent", "Pest Control",
            "Trash Bags", "Dish Sponge", "Scrubber", "Dustbin", "Broom",
            "Mop", "Bucket", "Duster", "Cleaning Cloth", "Disinfectant"
        };
        
        String[] brands = {"Surf Excel", "Vim", "Harpic", "Lizol", "Godrej"};
        return generateProducts(startId, items, brands, "Household & Cleaning", 40, 600, 0.1, 0.25);
    }

    private List<Product> generateProducts(int startId, String[] items, String[] brands, 
                                           String category, double minPrice, double maxPrice,
                                           double minDiscount, double maxDiscount) {
        List<Product> products = new ArrayList<>();
        int currentId = startId;

        for (String item : items) {
            String brand = brands[random.nextInt(brands.length)];
            double basePrice = minPrice + (maxPrice - minPrice) * random.nextDouble();
            double discountPercent = minDiscount + (maxDiscount - minDiscount) * random.nextDouble();
            double discountedPrice = basePrice * (1 - discountPercent);
            
            // Generate SKU code
            String skuCode = generateSKUCode(category, currentId);
            
            // Generate image URL (using Picsum Photos for reliable placeholder images)
            String imageUrl = generateImageUrl(item, category);
            
            // Generate description
            String description = generateDescription(item, brand, category);
            
            // Rating between 3.5 and 5.0
            double rating = 3.5 + (1.5 * random.nextDouble());
            
            // Some products are popular (30% chance)
            boolean isPopular = random.nextDouble() < 0.3;
            
            // Stock between 10 and 500
            int stock = 10 + random.nextInt(490);

            Product product = Product.builder()
                    .productId(currentId)
                    .productName(item)
                    .brand(brand)
                    .category(category)
                    .price(Math.round(basePrice * 100.0) / 100.0)
                    .discountedPrice(Math.round(discountedPrice * 100.0) / 100.0)
                    .availableStock(stock)
                    .skuCode(skuCode)
                    .imageUrl(imageUrl)
                    .description(description)
                    .rating(Math.round(rating * 10.0) / 10.0)
                    .isPopular(isPopular)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            products.add(product);
            currentId++;
        }

        return products;
    }

    private String generateSKUCode(String category, int productId) {
        String categoryCode = category.substring(0, Math.min(3, category.length())).toUpperCase()
                .replace(" ", "")
                .replace("&", "");
        return String.format("%s-%04d", categoryCode, productId);
    }

    private String generateImageUrl(String itemName, String category) {
        // Convert product name to Wikimedia Commons filename format
        String filename = convertToWikimediaFilename(itemName);
        // Use Wikimedia Commons Special:FilePath for direct image access
        return String.format("https://commons.wikimedia.org/wiki/Special:FilePath/%s", filename);
    }
    
    private String convertToWikimediaFilename(String productName) {
        // Clean product name: remove parentheses, extra text, convert to filename format
        String cleaned = productName
                .replaceAll("\\(.*?\\)", "") // Remove text in parentheses
                .replaceAll("\\s+", "_") // Replace spaces with underscores
                .trim();
        
        // Handle common product name variations
        String lower = cleaned.toLowerCase();
        
        // Fruits & Vegetables
        if (lower.contains("potato")) return "Potatoes.jpg";
        if (lower.contains("onion")) return "Onion_on_White.JPG";
        if (lower.contains("tomato")) return "Tomato.jpg";
        if (lower.contains("carrot")) return "Carrots.jpg";
        if (lower.contains("capsicum") || lower.contains("bell_pepper")) return "Bell_pepper.jpg";
        if (lower.contains("cauliflower")) return "Cauliflower.jpg";
        if (lower.contains("cabbage")) return "Cabbage.jpg";
        if (lower.contains("brinjal") || lower.contains("eggplant")) return "Eggplant.jpg";
        if (lower.contains("okra") || lower.contains("lady_finger")) return "Okra.jpg";
        if (lower.contains("bottle_gourd") || lower.contains("lauki")) return "Bottle_gourd.jpg";
        if (lower.contains("cucumber")) return "Cucumber.jpg";
        if (lower.contains("radish")) return "Radish.jpg";
        if (lower.contains("beetroot") || lower.contains("beet")) return "Beetroot.jpg";
        if (lower.contains("spinach") || lower.contains("palak")) return "Spinach.jpg";
        if (lower.contains("coriander") || lower.contains("cilantro")) return "Coriander.jpg";
        if (lower.contains("mint")) return "Mint.jpg";
        if (lower.contains("curry_leaves")) return "Curry_leaves.jpg";
        if (lower.contains("chili") || lower.contains("chilli")) return "Green_chili.jpg";
        if (lower.contains("ginger") || lower.contains("adrak")) return "Ginger.jpg";
        if (lower.contains("garlic") || lower.contains("lehsun")) return "Garlic.jpg";
        if (lower.contains("broccoli")) return "Broccoli.jpg";
        if (lower.contains("mushroom")) return "Mushroom.jpg";
        if (lower.contains("green_peas") || lower.contains("matar")) return "Green_peas.jpg";
        
        // Fruits
        if (lower.contains("apple")) return "Apple.jpg";
        if (lower.contains("banana")) return "Banana.jpg";
        if (lower.contains("orange")) return "Orange.jpg";
        if (lower.contains("grape")) return "Grapes.jpg";
        if (lower.contains("pomegranate") || lower.contains("anar")) return "Pomegranate.jpg";
        if (lower.contains("mango")) return "Mango.jpg";
        if (lower.contains("watermelon")) return "Watermelon.jpg";
        if (lower.contains("muskmelon") || lower.contains("cantaloupe")) return "Cantaloupe.jpg";
        if (lower.contains("papaya")) return "Papaya.jpg";
        if (lower.contains("guava") || lower.contains("amrud")) return "Guava.jpg";
        if (lower.contains("pineapple")) return "Pineapple.jpg";
        if (lower.contains("strawberry")) return "Strawberry.jpg";
        if (lower.contains("kiwi")) return "Kiwi.jpg";
        if (lower.contains("lime") || lower.contains("mosambi")) return "Lime.jpg";
        if (lower.contains("custard_apple")) return "Custard_apple.jpg";
        if (lower.contains("dragon_fruit")) return "Dragon_fruit.jpg";
        if (lower.contains("avocado")) return "Avocado.jpg";
        if (lower.contains("pear")) return "Pear.jpg";
        if (lower.contains("plum")) return "Plum.jpg";
        if (lower.contains("cherry")) return "Cherry.jpg";
        if (lower.contains("litchi")) return "Litchi.jpg";
        
        // Grains & Pulses
        if (lower.contains("rice")) return "Rice.jpg";
        if (lower.contains("wheat") || lower.contains("atta") || lower.contains("flour")) return "Wheat.jpg";
        if (lower.contains("gram_flour") || lower.contains("besan")) return "Gram_flour.jpg";
        if (lower.contains("ragi")) return "Ragi.jpg";
        if (lower.contains("bajra")) return "Bajra.jpg";
        if (lower.contains("jowar")) return "Jowar.jpg";
        if (lower.contains("dal") || lower.contains("lentil")) {
            if (lower.contains("toor") || lower.contains("arhar")) return "Toor_dal.jpg";
            if (lower.contains("moong")) return "Moong_dal.jpg";
            if (lower.contains("chana")) return "Chana_dal.jpg";
            if (lower.contains("urad")) return "Urad_dal.jpg";
            if (lower.contains("masoor")) return "Masoor_dal.jpg";
            return "Lentil.jpg";
        }
        if (lower.contains("rajma") || lower.contains("kidney_bean")) return "Kidney_beans.jpg";
        if (lower.contains("quinoa")) return "Quinoa.jpg";
        if (lower.contains("barley")) return "Barley.jpg";
        if (lower.contains("oats")) return "Oats.jpg";
        if (lower.contains("millet")) return "Millet.jpg";
        if (lower.contains("buckwheat")) return "Buckwheat.jpg";
        if (lower.contains("semolina") || lower.contains("suji") || lower.contains("rava")) return "Semolina.jpg";
        if (lower.contains("couscous")) return "Couscous.jpg";
        if (lower.contains("bulgur")) return "Bulgur.jpg";
        if (lower.contains("pasta")) return "Pasta.jpg";
        
        // Spices & Condiments
        if (lower.contains("turmeric")) return "Turmeric.jpg";
        if (lower.contains("chili_powder") || lower.contains("red_chilli")) return "Red_chili_powder.jpg";
        if (lower.contains("coriander_powder")) return "Coriander_powder.jpg";
        if (lower.contains("cumin") || lower.contains("jeera")) return "Cumin.jpg";
        if (lower.contains("garam_masala")) return "Garam_masala.jpg";
        if (lower.contains("pepper") || lower.contains("black_pepper")) return "Black_pepper.jpg";
        if (lower.contains("mustard")) return "Mustard_seeds.jpg";
        if (lower.contains("fenugreek")) return "Fenugreek.jpg";
        if (lower.contains("fennel")) return "Fennel.jpg";
        if (lower.contains("cardamom")) return "Cardamom.jpg";
        if (lower.contains("cinnamon")) return "Cinnamon.jpg";
        if (lower.contains("clove")) return "Cloves.jpg";
        if (lower.contains("bay_leaf")) return "Bay_leaves.jpg";
        if (lower.contains("star_anise")) return "Star_anise.jpg";
        if (lower.contains("saffron")) return "Saffron.jpg";
        if (lower.contains("asafoetida") || lower.contains("hing")) return "Asafoetida.jpg";
        if (lower.contains("tamarind")) return "Tamarind.jpg";
        if (lower.contains("masala")) return "Spice_mix.jpg";
        if (lower.contains("salt")) return "Salt.jpg";
        if (lower.contains("sugar")) return "Sugar.jpg";
        
        // Dairy Products
        if (lower.contains("milk")) return "Milk.jpg";
        if (lower.contains("curd") || lower.contains("yogurt") || lower.contains("dahi")) return "Yogurt.jpg";
        if (lower.contains("paneer") || lower.contains("cottage_cheese")) return "Paneer.jpg";
        if (lower.contains("butter")) return "Butter.jpg";
        if (lower.contains("ghee")) return "Ghee.jpg";
        if (lower.contains("cheese")) {
            if (lower.contains("mozzarella")) return "Mozzarella.jpg";
            if (lower.contains("cheddar")) return "Cheddar_cheese.jpg";
            return "Cheese.jpg";
        }
        if (lower.contains("cream")) return "Cream.jpg";
        if (lower.contains("buttermilk")) return "Buttermilk.jpg";
        if (lower.contains("ice_cream")) return "Ice_cream.jpg";
        if (lower.contains("kulfi")) return "Kulfi.jpg";
        
        // Beverages
        if (lower.contains("tea")) return "Tea.jpg";
        if (lower.contains("coffee")) return "Coffee.jpg";
        if (lower.contains("juice")) {
            if (lower.contains("orange")) return "Orange_juice.jpg";
            if (lower.contains("apple")) return "Apple_juice.jpg";
            if (lower.contains("mango")) return "Mango_juice.jpg";
            return "Fruit_juice.jpg";
        }
        if (lower.contains("coconut_water")) return "Coconut_water.jpg";
        if (lower.contains("lemonade")) return "Lemonade.jpg";
        if (lower.contains("lassi")) return "Lassi.jpg";
        if (lower.contains("smoothie")) return "Smoothie.jpg";
        if (lower.contains("energy_drink")) return "Energy_drink.jpg";
        if (lower.contains("soft_drink") || lower.contains("cola")) return "Soft_drink.jpg";
        if (lower.contains("water") && !lower.contains("coconut")) return "Mineral_water.jpg";
        
        // Snacks & Sweets
        if (lower.contains("chip")) return "Potato_chips.jpg";
        if (lower.contains("kurkure")) return "Kurkure.jpg";
        if (lower.contains("namkeen") || lower.contains("mixture")) return "Namkeen.jpg";
        if (lower.contains("bhujia")) return "Bhujia.jpg";
        if (lower.contains("biscuit") || lower.contains("cookie")) return "Biscuit.jpg";
        if (lower.contains("chocolate")) {
            if (lower.contains("dark")) return "Dark_chocolate.jpg";
            if (lower.contains("white")) return "White_chocolate.jpg";
            return "Chocolate.jpg";
        }
        if (lower.contains("candy") || lower.contains("toffee")) return "Candy.jpg";
        if (lower.contains("gulab_jamun")) return "Gulab_jamun.jpg";
        if (lower.contains("rasgulla")) return "Rasgulla.jpg";
        if (lower.contains("barfi")) return "Barfi.jpg";
        if (lower.contains("ladoo")) return "Ladoo.jpg";
        if (lower.contains("jalebi")) return "Jalebi.jpg";
        if (lower.contains("cake")) return "Cake.jpg";
        if (lower.contains("pastry")) return "Pastry.jpg";
        if (lower.contains("donut")) return "Donut.jpg";
        if (lower.contains("muffin")) return "Muffin.jpg";
        if (lower.contains("brownie")) return "Brownie.jpg";
        if (lower.contains("popcorn")) return "Popcorn.jpg";
        if (lower.contains("nachos")) return "Nachos.jpg";
        if (lower.contains("protein_bar")) return "Protein_bar.jpg";
        if (lower.contains("trail_mix")) return "Trail_mix.jpg";
        
        // Personal Care
        if (lower.contains("soap")) return "Soap.jpg";
        if (lower.contains("body_wash")) return "Body_wash.jpg";
        if (lower.contains("shampoo")) return "Shampoo.jpg";
        if (lower.contains("conditioner")) return "Conditioner.jpg";
        if (lower.contains("face_wash")) return "Face_wash.jpg";
        if (lower.contains("toothpaste")) return "Toothpaste.jpg";
        if (lower.contains("toothbrush")) return "Toothbrush.jpg";
        if (lower.contains("mouthwash")) return "Mouthwash.jpg";
        if (lower.contains("sanitizer")) return "Hand_sanitizer.jpg";
        if (lower.contains("deodorant")) return "Deodorant.jpg";
        if (lower.contains("perfume")) return "Perfume.jpg";
        if (lower.contains("lotion")) return "Body_lotion.jpg";
        if (lower.contains("cream") && !lower.contains("ice")) {
            if (lower.contains("face")) return "Face_cream.jpg";
            if (lower.contains("moisturizing")) return "Moisturizing_cream.jpg";
            return "Cream.jpg";
        }
        if (lower.contains("sunscreen")) return "Sunscreen.jpg";
        if (lower.contains("hair_oil")) return "Hair_oil.jpg";
        if (lower.contains("shaving")) return "Shaving_cream.jpg";
        if (lower.contains("razor")) return "Razor.jpg";
        if (lower.contains("cotton_swab")) return "Cotton_swab.jpg";
        if (lower.contains("tissue")) return "Tissue_paper.jpg";
        if (lower.contains("talcum_powder")) return "Talcum_powder.jpg";
        if (lower.contains("lipstick")) return "Lipstick.jpg";
        if (lower.contains("kajal")) return "Kajal.jpg";
        if (lower.contains("bb_cream")) return "BB_cream.jpg";
        if (lower.contains("nail_polish")) return "Nail_polish.jpg";
        if (lower.contains("hair_serum")) return "Hair_serum.jpg";
        
        // Household & Cleaning
        if (lower.contains("detergent")) return "Detergent.jpg";
        if (lower.contains("dish_soap") || lower.contains("dishwash")) return "Dish_soap.jpg";
        if (lower.contains("floor_cleaner")) return "Floor_cleaner.jpg";
        if (lower.contains("toilet_cleaner")) return "Toilet_cleaner.jpg";
        if (lower.contains("glass_cleaner")) return "Glass_cleaner.jpg";
        if (lower.contains("polish")) return "Furniture_polish.jpg";
        if (lower.contains("air_freshener")) return "Air_freshener.jpg";
        if (lower.contains("mosquito") || lower.contains("repellent")) return "Mosquito_repellent.jpg";
        if (lower.contains("pest_control")) return "Pest_control.jpg";
        if (lower.contains("trash_bag") || lower.contains("garbage_bag")) return "Trash_bag.jpg";
        if (lower.contains("sponge")) return "Dish_sponge.jpg";
        if (lower.contains("scrubber")) return "Scrubber.jpg";
        if (lower.contains("dustbin") || lower.contains("trash_can")) return "Dustbin.jpg";
        if (lower.contains("broom")) return "Broom.jpg";
        if (lower.contains("mop")) return "Mop.jpg";
        if (lower.contains("bucket")) return "Bucket.jpg";
        if (lower.contains("duster")) return "Duster.jpg";
        if (lower.contains("cleaning_cloth") || lower.contains("rag")) return "Cleaning_cloth.jpg";
        if (lower.contains("disinfectant")) return "Disinfectant.jpg";
        if (lower.contains("washing_powder")) return "Washing_powder.jpg";
        if (lower.contains("kitchen_wipes")) return "Kitchen_wipes.jpg";
        
        // Oils
        if (lower.contains("cooking_oil") || lower.contains("sunflower_oil")) return "Cooking_oil.jpg";
        if (lower.contains("olive_oil")) return "Olive_oil.jpg";
        if (lower.contains("mustard_oil")) return "Mustard_oil.jpg";
        if (lower.contains("coconut_oil")) return "Coconut_oil.jpg";
        
        // Other
        if (lower.contains("honey")) return "Honey.jpg";
        if (lower.contains("peanut_butter")) return "Peanut_butter.jpg";
        if (lower.contains("cereal") || lower.contains("cornflakes")) return "Cereal.jpg";
        if (lower.contains("noodles") || lower.contains("maggi")) return "Noodles.jpg";
        if (lower.contains("chicken")) return "Chicken.jpg";
        if (lower.contains("multivitamin") || lower.contains("vitamin")) return "Multivitamin.jpg";
        if (lower.contains("protein_powder") || lower.contains("whey")) return "Protein_powder.jpg";
        if (lower.contains("omega") || lower.contains("fish_oil")) return "Fish_oil.jpg";
        
        // Electronics (fallback to generic)
        if (lower.contains("earbud") || lower.contains("headphone")) return "Headphones.jpg";
        if (lower.contains("tv") || lower.contains("television")) return "TV.jpg";
        if (lower.contains("speaker")) return "Speaker.jpg";
        if (lower.contains("power_bank")) return "Power_bank.jpg";
        if (lower.contains("mouse")) return "Mouse.jpg";
        if (lower.contains("cable") || lower.contains("usb")) return "USB_cable.jpg";
        if (lower.contains("watch") || lower.contains("fitness")) return "Smart_watch.jpg";
        if (lower.contains("keyboard")) return "Keyboard.jpg";
        if (lower.contains("webcam")) return "Webcam.jpg";
        if (lower.contains("pendrive")) return "Pendrive.jpg";
        if (lower.contains("extension_cord")) return "Extension_cord.jpg";
        if (lower.contains("bulb") || lower.contains("led")) return "LED_bulb.jpg";
        if (lower.contains("fan")) return "Fan.jpg";
        if (lower.contains("iron")) return "Iron.jpg";
        if (lower.contains("water_heater")) return "Water_heater.jpg";
        if (lower.contains("charger")) return "Charger.jpg";
        if (lower.contains("lunch_box")) return "Lunch_box.jpg";
        if (lower.contains("water_bottle")) return "Water_bottle.jpg";
        if (lower.contains("frying_pan") || lower.contains("non_stick")) return "Frying_pan.jpg";
        
        // Default: convert cleaned name to filename format
        return cleaned.substring(0, Math.min(cleaned.length(), 50)) + ".jpg";
    }
    
    private String getCategoryColor(String category) {
        // Assign different colors to different categories for visual distinction
        if (category == null) return "FFD700"; // Gold default
        
        String lowerCategory = category.toLowerCase();
        
        // Fruits & Vegetables
        if (lowerCategory.contains("fruit") || lowerCategory.contains("vegetable") || 
            lowerCategory.contains("vegetables")) return "4CAF50"; // Green
        
        // Grains & Pulses
        if (lowerCategory.contains("grain") || lowerCategory.contains("pulse") || 
            lowerCategory.contains("rice") || lowerCategory.contains("dal") || 
            lowerCategory.contains("flour")) return "FF9800"; // Orange
        
        // Spices & Condiments
        if (lowerCategory.contains("spice") || lowerCategory.contains("condiment") ||
            lowerCategory.contains("masala")) return "F44336"; // Red
        
        // Dairy Products
        if (lowerCategory.contains("dairy") || lowerCategory.contains("milk") ||
            lowerCategory.contains("cheese") || lowerCategory.contains("butter")) return "2196F3"; // Blue
        
        // Beverages
        if (lowerCategory.contains("beverage") || lowerCategory.contains("drink") ||
            lowerCategory.contains("tea") || lowerCategory.contains("coffee") ||
            lowerCategory.contains("juice")) return "9C27B0"; // Purple
        
        // Snacks & Sweets
        if (lowerCategory.contains("snack") || lowerCategory.contains("sweet") ||
            lowerCategory.contains("chocolate") || lowerCategory.contains("biscuit") ||
            lowerCategory.contains("chip")) return "FFC107"; // Amber
        
        // Personal Care
        if (lowerCategory.contains("personal") || lowerCategory.contains("care") ||
            lowerCategory.contains("soap") || lowerCategory.contains("shampoo") ||
            lowerCategory.contains("toothpaste")) return "00BCD4"; // Cyan
        
        // Household & Cleaning
        if (lowerCategory.contains("household") || lowerCategory.contains("cleaning") ||
            lowerCategory.contains("detergent") || lowerCategory.contains("cleaner")) return "607D8B"; // Blue Grey
        
        return "FFD700"; // Gold default
    }
    
    private String getImageSearchTerm(String itemName, String category) {
        // Create a map of product names to image search terms for accurate matching
        String lowerName = itemName.toLowerCase();
        
        // Fruits & Vegetables
        if (category.equals("Fruits & Vegetables")) {
            if (lowerName.contains("tomato")) return "tomato fresh vegetable";
            if (lowerName.contains("potato")) return "potato vegetable fresh";
            if (lowerName.contains("onion")) return "onion vegetable fresh";
            if (lowerName.contains("carrot")) return "carrot fresh vegetable";
            if (lowerName.contains("capsicum") || lowerName.contains("bell pepper")) return "bell pepper vegetable fresh";
            if (lowerName.contains("cauliflower")) return "cauliflower fresh vegetable";
            if (lowerName.contains("cabbage")) return "cabbage fresh vegetable";
            if (lowerName.contains("brinjal") || lowerName.contains("eggplant")) return "eggplant fresh vegetable";
            if (lowerName.contains("okra") || lowerName.contains("lady finger")) return "okra fresh vegetable";
            if (lowerName.contains("bottle gourd") || lowerName.contains("calabash")) return "bottle gourd vegetable";
            if (lowerName.contains("cucumber")) return "cucumber fresh vegetable";
            if (lowerName.contains("radish")) return "radish fresh vegetable";
            if (lowerName.contains("beetroot") || lowerName.contains("beet")) return "beetroot fresh vegetable";
            if (lowerName.contains("spinach")) return "spinach fresh green vegetable";
            if (lowerName.contains("coriander") || lowerName.contains("cilantro")) return "coriander fresh herb";
            if (lowerName.contains("mint")) return "mint fresh herb";
            if (lowerName.contains("curry leaves")) return "curry leaves fresh herb";
            if (lowerName.contains("chili") || lowerName.contains("chilli")) return "green chili fresh vegetable";
            if (lowerName.contains("ginger")) return "ginger fresh root";
            if (lowerName.contains("garlic")) return "garlic fresh vegetable";
            if (lowerName.contains("apple")) return "apple fresh fruit";
            if (lowerName.contains("banana")) return "banana fresh fruit";
            if (lowerName.contains("orange")) return "orange fresh fruit";
            if (lowerName.contains("grape")) return "grapes fresh fruit";
            if (lowerName.contains("pomegranate")) return "pomegranate fresh fruit";
            if (lowerName.contains("mango")) return "mango fresh fruit";
            if (lowerName.contains("watermelon")) return "watermelon fresh fruit";
            if (lowerName.contains("muskmelon") || lowerName.contains("cantaloupe")) return "cantaloupe fresh fruit";
            if (lowerName.contains("papaya")) return "papaya fresh fruit";
            if (lowerName.contains("guava")) return "guava fresh fruit";
            if (lowerName.contains("pineapple")) return "pineapple fresh fruit";
            if (lowerName.contains("strawberr")) return "strawberry fresh fruit";
            if (lowerName.contains("kiwi")) return "kiwi fresh fruit";
            if (lowerName.contains("lime") || lowerName.contains("sweet lime")) return "lime fresh fruit";
            if (lowerName.contains("custard apple") || lowerName.contains("sugar apple")) return "custard apple fruit";
            if (lowerName.contains("dragon fruit")) return "dragon fruit fresh";
            if (lowerName.contains("avocado")) return "avocado fresh fruit";
            if (lowerName.contains("pear")) return "pear fresh fruit";
            if (lowerName.contains("plum")) return "plum fresh fruit";
            if (lowerName.contains("cherry")) return "cherry fresh fruit";
        }
        
        // Grains & Pulses
        if (category.equals("Grains & Pulses")) {
            if (lowerName.contains("rice")) return "rice grain food";
            if (lowerName.contains("wheat") || lowerName.contains("flour")) return "wheat flour grain";
            if (lowerName.contains("gram flour") || lowerName.contains("besan")) return "gram flour pulse";
            if (lowerName.contains("ragi") || lowerName.contains("finger millet")) return "ragi flour grain";
            if (lowerName.contains("bajra") || lowerName.contains("pearl millet")) return "bajra flour grain";
            if (lowerName.contains("jowar") || lowerName.contains("sorghum")) return "jowar flour grain";
            if (lowerName.contains("dal") || lowerName.contains("lentil")) return "lentil pulse food";
            if (lowerName.contains("toor") || lowerName.contains("pigeon pea")) return "toor dal pulse";
            if (lowerName.contains("moong") || lowerName.contains("mung")) return "moong dal pulse";
            if (lowerName.contains("chana") || lowerName.contains("chickpea")) return "chana dal pulse";
            if (lowerName.contains("urad") || lowerName.contains("black gram")) return "urad dal pulse";
            if (lowerName.contains("masoor") || lowerName.contains("red lentil")) return "masoor dal pulse";
            if (lowerName.contains("rajma") || lowerName.contains("kidney bean")) return "kidney beans pulse";
            if (lowerName.contains("quinoa")) return "quinoa grain food";
            if (lowerName.contains("barley")) return "barley grain";
            if (lowerName.contains("oats")) return "oats grain breakfast";
            if (lowerName.contains("millet")) return "millet grain";
            if (lowerName.contains("buckwheat")) return "buckwheat grain";
            if (lowerName.contains("semolina") || lowerName.contains("suji")) return "semolina grain";
            if (lowerName.contains("couscous")) return "couscous grain";
            if (lowerName.contains("bulgur")) return "bulgur grain";
        }
        
        // Spices & Condiments
        if (category.equals("Spices & Condiments")) {
            if (lowerName.contains("turmeric")) return "turmeric spice powder";
            if (lowerName.contains("chili powder") || lowerName.contains("red chili")) return "red chili powder spice";
            if (lowerName.contains("coriander powder")) return "coriander powder spice";
            if (lowerName.contains("cumin")) return "cumin spice seeds";
            if (lowerName.contains("garam masala")) return "garam masala spice";
            if (lowerName.contains("pepper") || lowerName.contains("black pepper")) return "black pepper spice";
            if (lowerName.contains("mustard")) return "mustard seeds spice";
            if (lowerName.contains("fenugreek")) return "fenugreek seeds spice";
            if (lowerName.contains("fennel")) return "fennel seeds spice";
            if (lowerName.contains("cardamom")) return "cardamom spice";
            if (lowerName.contains("cinnamon")) return "cinnamon spice stick";
            if (lowerName.contains("clove")) return "cloves spice";
            if (lowerName.contains("bay leaf") || lowerName.contains("bay leaves")) return "bay leaves spice";
            if (lowerName.contains("star anise")) return "star anise spice";
            if (lowerName.contains("saffron")) return "saffron spice";
            if (lowerName.contains("asafoetida") || lowerName.contains("hing")) return "asafoetida spice";
            if (lowerName.contains("tamarind")) return "tamarind spice";
            if (lowerName.contains("masala")) return "spice mix masala";
        }
        
        // Dairy Products
        if (category.equals("Dairy Products")) {
            if (lowerName.contains("milk")) return "milk dairy bottle";
            if (lowerName.contains("curd") || lowerName.contains("yogurt")) return "yogurt dairy";
            if (lowerName.contains("paneer") || lowerName.contains("cottage cheese")) return "paneer dairy cheese";
            if (lowerName.contains("butter")) return "butter dairy";
            if (lowerName.contains("ghee")) return "ghee dairy";
            if (lowerName.contains("cheese")) return "cheese dairy";
            if (lowerName.contains("cream")) return "cream dairy";
            if (lowerName.contains("buttermilk")) return "buttermilk dairy";
            if (lowerName.contains("ice cream")) return "ice cream dairy";
            if (lowerName.contains("kulfi")) return "kulfi dairy ice cream";
        }
        
        // Beverages
        if (category.equals("Beverages")) {
            if (lowerName.contains("tea")) return "tea beverage hot";
            if (lowerName.contains("coffee")) return "coffee beverage hot";
            if (lowerName.contains("juice")) return "fruit juice beverage";
            if (lowerName.contains("coconut water")) return "coconut water beverage";
            if (lowerName.contains("lemonade")) return "lemonade beverage";
            if (lowerName.contains("lassi")) return "lassi beverage";
            if (lowerName.contains("smoothie")) return "smoothie beverage";
            if (lowerName.contains("energy drink")) return "energy drink beverage";
            if (lowerName.contains("soft drink") || lowerName.contains("cola")) return "soft drink beverage";
            if (lowerName.contains("water") && !lowerName.contains("coconut")) return "mineral water beverage";
        }
        
        // Snacks & Sweets
        if (category.equals("Snacks & Sweets")) {
            if (lowerName.contains("chip")) return "potato chips snack";
            if (lowerName.contains("kurkure")) return "kurkure snack";
            if (lowerName.contains("namkeen") || lowerName.contains("mixture")) return "namkeen snack";
            if (lowerName.contains("bhujia")) return "bhujia snack";
            if (lowerName.contains("biscuit") || lowerName.contains("cookie")) return "biscuit snack";
            if (lowerName.contains("chocolate")) return "chocolate sweet";
            if (lowerName.contains("candy") || lowerName.contains("toffee")) return "candy sweet";
            if (lowerName.contains("gulab jamun")) return "gulab jamun sweet";
            if (lowerName.contains("rasgulla")) return "rasgulla sweet";
            if (lowerName.contains("barfi")) return "barfi sweet";
            if (lowerName.contains("ladoo")) return "ladoo sweet";
            if (lowerName.contains("jalebi")) return "jalebi sweet";
            if (lowerName.contains("cake")) return "cake sweet";
            if (lowerName.contains("pastry")) return "pastry sweet";
            if (lowerName.contains("donut")) return "donut sweet";
            if (lowerName.contains("muffin")) return "muffin sweet";
            if (lowerName.contains("brownie")) return "brownie sweet";
        }
        
        // Personal Care
        if (category.equals("Personal Care")) {
            if (lowerName.contains("soap")) return "soap personal care";
            if (lowerName.contains("body wash")) return "body wash personal care";
            if (lowerName.contains("shampoo")) return "shampoo personal care";
            if (lowerName.contains("conditioner")) return "conditioner personal care";
            if (lowerName.contains("face wash")) return "face wash personal care";
            if (lowerName.contains("toothpaste")) return "toothpaste personal care";
            if (lowerName.contains("toothbrush")) return "toothbrush personal care";
            if (lowerName.contains("mouthwash")) return "mouthwash personal care";
            if (lowerName.contains("sanitizer")) return "hand sanitizer personal care";
            if (lowerName.contains("deodorant")) return "deodorant personal care";
            if (lowerName.contains("perfume")) return "perfume personal care";
            if (lowerName.contains("lotion")) return "body lotion personal care";
            if (lowerName.contains("cream") && !lowerName.contains("ice")) return "face cream personal care";
            if (lowerName.contains("sunscreen")) return "sunscreen personal care";
            if (lowerName.contains("hair oil")) return "hair oil personal care";
            if (lowerName.contains("shaving")) return "shaving cream personal care";
            if (lowerName.contains("razor")) return "razor personal care";
            if (lowerName.contains("cotton swab") || lowerName.contains("ear bud")) return "cotton swab personal care";
            if (lowerName.contains("tissue")) return "tissue paper personal care";
        }
        
        // Household & Cleaning
        if (category.equals("Household & Cleaning")) {
            if (lowerName.contains("detergent")) return "detergent cleaning";
            if (lowerName.contains("dish soap") || lowerName.contains("dishwash")) return "dish soap cleaning";
            if (lowerName.contains("floor cleaner")) return "floor cleaner cleaning";
            if (lowerName.contains("toilet cleaner")) return "toilet cleaner cleaning";
            if (lowerName.contains("glass cleaner")) return "glass cleaner cleaning";
            if (lowerName.contains("polish")) return "furniture polish cleaning";
            if (lowerName.contains("air freshener")) return "air freshener cleaning";
            if (lowerName.contains("mosquito") || lowerName.contains("repellent")) return "mosquito repellent cleaning";
            if (lowerName.contains("pest control")) return "pest control cleaning";
            if (lowerName.contains("trash bag") || lowerName.contains("garbage bag")) return "trash bag cleaning";
            if (lowerName.contains("sponge")) return "dish sponge cleaning";
            if (lowerName.contains("scrubber")) return "scrubber cleaning";
            if (lowerName.contains("dustbin") || lowerName.contains("trash can")) return "dustbin cleaning";
            if (lowerName.contains("broom")) return "broom cleaning";
            if (lowerName.contains("mop")) return "mop cleaning";
            if (lowerName.contains("bucket")) return "bucket cleaning";
            if (lowerName.contains("duster")) return "duster cleaning";
            if (lowerName.contains("cleaning cloth") || lowerName.contains("rag")) return "cleaning cloth cleaning";
            if (lowerName.contains("disinfectant")) return "disinfectant cleaning";
        }
        
        // Fallback: use product name as search term
        return itemName.toLowerCase().replace(" ", " ");
    }

    private String generateDescription(String itemName, String brand, String category) {
        return String.format("Premium quality %s from %s. Fresh, authentic, and carefully selected for your %s needs. " +
                "Perfect for everyday use. High quality product with great value for money.",
                itemName.toLowerCase(), brand, category.toLowerCase());
    }
}

