// Script to update all products in MongoDB with stock
const { MongoClient } = require('mongodb');

const MONGO_URI = 'mongodb://localhost:27017';
const DB_NAME = 'dealshare';

async function updateInventory() {
  const client = new MongoClient(MONGO_URI);
  
  try {
    await client.connect();
    console.log('✅ Connected to MongoDB');
    
    const db = client.db(DB_NAME);
    const productsCollection = db.collection('products');
    
    // Update all products to have stock
    const result = await productsCollection.updateMany(
      {},
      {
        $set: {
          available_stock: { $cond: { if: { $lte: ["$available_stock", 0] }, then: 100, else: "$available_stock" } }
        }
      }
    );
    
    // Set random stock between 50-200 for all products
    const products = await productsCollection.find({}).toArray();
    
    for (const product of products) {
      const randomStock = Math.floor(Math.random() * 150) + 50; // 50-200
      await productsCollection.updateOne(
        { _id: product._id },
        { $set: { available_stock: randomStock } }
      );
    }
    
    console.log(`✅ Updated ${products.length} products with inventory`);
    
    // Verify
    const inStockCount = await productsCollection.countDocuments({ available_stock: { $gt: 0 } });
    console.log(`✅ ${inStockCount} products now have stock!`);
    
  } catch (error) {
    console.error('❌ Error:', error);
  } finally {
    await client.close();
  }
}

updateInventory();
