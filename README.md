# 🏪 Halli-Santhe Digital — Local Artisan Marketplace

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen?style=for-the-badge&logo=android" />
  <img src="https://img.shields.io/badge/Language-Kotlin-blue?style=for-the-badge&logo=kotlin" />
  <img src="https://img.shields.io/badge/Database-Firebase-orange?style=for-the-badge&logo=firebase" />
  <img src="https://img.shields.io/badge/Min%20SDK-API%2024-red?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Version-1.0-purple?style=for-the-badge" />
</p>

<p align="center">
  <b>ಹಳ್ಳಿ ಸಂತೆ | हाली संते | Halli-Santhe</b><br/>
  <i>Your Local Digital Market 🌿 — Vocal for Local 🇮🇳</i>
</p>

---

## 📌 Problem Statement

Local artisans across Karnataka villages have **limited reach**, selling only at weekly physical markets. Buyers don't know what products are available until they travel to the market. Traditional crafts like **Channapatna toys, pottery, and handloom textiles** are at risk of disappearing due to low sales and poor digital visibility.

**Halli-Santhe Digital** solves this by creating a digital catalog — making traditional handmade goods discoverable online, connecting village artisans directly to urban buyers **without any middlemen**.

---

## 🎯 Solution

A hyper-local **Android marketplace app** where:
- 🧑‍🎨 **Artisans** upload and manage their handmade products
- 🛍️ **Buyers** discover, browse, and contact artisans directly via WhatsApp or call
- 🌐 **Multi-language** support makes it accessible to all (English, Kannada, Hindi)

---

## ✨ Key Features

### 🛍️ Buyer Features
| Feature | Description |
|---------|-------------|
| 🔍 Real-time Search | Search products by name instantly |
| 🗂️ Category Filter | 14 art categories (Painting, Pottery, Handicraft, etc.) |
| ⇅ Sort | Sort by Price (Low/High) or Newest First |
| 💰 Price Range Filter | Slider to filter products by budget |
| ❤️ Wishlist | Save favourite products to view later |
| 💬 WhatsApp CTA | Contact artisan directly on WhatsApp |
| 📞 Call Seller | One-tap call to artisan |
| ⭐ Reviews & Ratings | Submit star ratings and reviews |
| 📤 Share Product | Share listings via WhatsApp, Instagram, etc. |
| 🚩 Report Product | Flag inappropriate listings |

### 🧑‍🎨 Artisan Features
| Feature | Description |
|---------|-------------|
| 📷 Upload Product | Add product with name, photo, price, category, location |
| 🔐 PIN Protection | 4-digit PIN to protect listings |
| ✏️ Edit Product | Update price, discount, description anytime |
| 🗑️ Delete Product | Remove listings when no longer available |
| ✅ Mark as Sold | Show SOLD badge on sold items |
| ⭐ Featured Toggle | Mark product as featured for priority display |
| 🚚 Delivery Toggle | Indicate if delivery is available |
| 📊 Sales History | View all sold items with total earnings |

### 🏪 Marketplace Features
| Feature | Description |
|---------|-------------|
| 🆕 New Arrivals | Horizontal section showing latest 5 products |
| ⭐ Featured Products | Priority showcase for featured listings |
| 🔥 Trending Now | Top products by view count |
| 🛍️ Similar Products | Related products shown in detail screen |
| 👁️ View Counter | Track how many times each product was viewed |
| 💬 Enquiry Counter | Track WhatsApp/Call enquiries per product |

### 📱 App Experience
| Feature | Description |
|---------|-------------|
| 🎯 Onboarding | 4-slide introduction for first-time users |
| 🌐 Multi-language | English, Kannada (ಕನ್ನಡ), Hindi (हिंदी) support |
| 🔄 Pull to Refresh | Swipe down to reload product grid |
| 💀 Loading Skeleton | Smooth loading animation |
| 📭 Empty State | Friendly screen when no products exist |
| 🖼️ Full-screen Image | Tap product image to view full screen |
| 📞 Contact Us | In-app support form via WhatsApp |

---

## 🖼️ Screenshots

> App running on Android device

| Home Screen | Product Detail | Add Product |
|:-----------:|:--------------:|:-----------:|
| Product grid with search, categories, sort | Full product info with WhatsApp CTA | Upload form with PIN protection |

| My Products | Artisan Profile | Wishlist |
|:-----------:|:---------------:|:--------:|
| PIN-protected artisan dashboard | Artisan stats & all products | Saved favourite products |

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| **Language** | Kotlin |
| **UI** | XML Layouts + RecyclerView |
| **Database** | Firebase Cloud Firestore |
| **Image Storage** | Base64 Encoding (stored in Firestore) |
| **Image Compression** | Custom `ImageCompressor` utility (800px, 90% JPEG) |
| **Multi-language** | Android Resource Qualifiers (`values-kn`, `values-hi`) |
| **Authentication** | 4-digit PIN system per artisan |
| **Build System** | Gradle KTS |
| **Version Control** | Git + GitHub |
| **Min SDK** | Android 7.0 (API 24) |
| **Target SDK** | Android 15 (API 36) |

---

## 📦 Dependencies

```kotlin
// Core
implementation("androidx.appcompat:appcompat:1.7.1")
implementation("androidx.recyclerview:recyclerview:1.4.0")
implementation("androidx.cardview:cardview:1.0.0")
implementation("androidx.core:core-ktx:1.15.0")

// UI Components
implementation("androidx.viewpager2:viewpager2:1.1.0")
implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

// Image Loading
implementation("com.github.bumptech.glide:glide:5.0.7")

// Firebase
implementation("com.google.firebase:firebase-firestore:26.2.0")
implementation("com.google.gms:google-services:4.4.0")
```

---

## 📁 Project Structure

```
HalliSanthe/
│
├── app/
│   ├── src/main/
│   │   ├── java/com/example/hallisanthe/
│   │   │   ├── adapter/
│   │   │   │   ├── ProductAdapter.kt         # Main product grid adapter
│   │   │   │   ├── MyProductAdapter.kt       # Artisan's product list adapter
│   │   │   │   ├── NewArrivalAdapter.kt      # New arrivals horizontal adapter
│   │   │   │   ├── FeaturedAdapter.kt        # Featured products adapter
│   │   │   │   ├── TrendingAdapter.kt        # Trending products adapter
│   │   │   │   ├── SimilarAdapter.kt         # Similar products adapter
│   │   │   │   ├── ReviewAdapter.kt          # Product reviews adapter
│   │   │   │   ├── SalesHistoryAdapter.kt    # Sales history adapter
│   │   │   │   └── OnboardingAdapter.kt      # Onboarding slides adapter
│   │   │   │
│   │   │   ├── model/
│   │   │   │   ├── Product.kt                # Product data model
│   │   │   │   └── Review.kt                 # Review data model
│   │   │   │
│   │   │   ├── utils/
│   │   │   │   ├── ImageCompressor.kt        # Image compression utility
│   │   │   │   └── LanguageHelper.kt         # Multi-language utility
│   │   │   │
│   │   │   ├── OnboardingActivity.kt         # First-time user intro
│   │   │   ├── SplashActivity.kt             # App loading screen
│   │   │   ├── MainActivity.kt               # Home screen
│   │   │   ├── DetailActivity.kt             # Product detail screen
│   │   │   ├── AddProductActivity.kt         # Artisan upload form
│   │   │   ├── MyProductsActivity.kt         # Artisan dashboard
│   │   │   ├── EditProductActivity.kt        # Edit product form
│   │   │   ├── ArtisanProfileActivity.kt     # Artisan profile & stats
│   │   │   ├── WishlistActivity.kt           # Saved favourites
│   │   │   ├── ReviewsActivity.kt            # Product reviews & ratings
│   │   │   ├── SalesHistoryActivity.kt       # Sold items history
│   │   │   └── ContactUsActivity.kt          # Support screen
│   │   │
│   │   └── res/
│   │       ├── layout/                       # All XML UI files
│   │       ├── drawable/                     # Backgrounds & shapes
│   │       ├── values/                       # English strings, colors, themes
│   │       ├── values-kn/                    # Kannada strings
│   │       ├── values-hi/                    # Hindi strings
│   │       └── mipmap/                       # App icons
│   │
│   ├── google-services.json                  # Firebase config (not tracked)
│   └── build.gradle.kts                      # App dependencies
│
├── build.gradle.kts                          # Project-level build config
├── settings.gradle.kts                       # Project settings
└── README.md                                 # This file
```

---

## ⚙️ Setup & Installation

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK API 24+
- Firebase account
- Git installed

### Step 1 — Clone the Repository
```bash
git clone https://github.com/synvishwas/HalliSanthe.git
cd HalliSanthe
```

### Step 2 — Firebase Setup
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a new project named `HalliSanthe`
3. Add an Android app with package name: `com.example.hallisanthe`
4. Download `google-services.json`
5. Place it in the `app/` folder

### Step 3 — Enable Firebase Services
```
Firebase Console:
→ Firestore Database → Create Database → Start in Test Mode
→ Region: asia-south1 (Mumbai)
```

### Step 4 — Open in Android Studio
```
File → Open → Select HalliSanthe folder
Wait for Gradle sync to complete
```

### Step 5 — Run the App
```
Connect Android device (USB debugging ON)
OR start an emulator

Click ▶ Run button in Android Studio
```

### Firestore Security Rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /products/{productId} {
      allow read, write: if true;
    }
    match /reviews/{reviewId} {
      allow read, write: if true;
    }
    match /reports/{reportId} {
      allow read, write: if true;
    }
  }
}
```

---

## 🗂️ Product Categories

```
1.  🖼️  Painting
2.  ✏️  Drawing & Sketch
3.  🪆  Handicraft
4.  🧵  Textile & Weaving
5.  🪴  Pottery & Clay
6.  🧸  Toys & Dolls
7.  💍  Jewellery
8.  🪵  Woodcraft
9.  🧶  Knitting & Crochet
10. 🍯  Organic Food
11. 🌾  Farming Produce
12. 🪡  Embroidery
13. 🎭  Masks & Decor
14. 📦  Other
```

---

## 🔁 App Flow

```
📱 Launch App
    ↓
🎯 Onboarding (first time only — 4 slides)
    ↓
🎨 Splash Screen (2.5 seconds)
    ↓
🏪 Home Screen
    ├── 🔍 Search products
    ├── 🗂️ Filter by category
    ├── ⇅ Sort by price/newest
    ├── 💰 Price range filter
    ├── 🆕 New Arrivals section
    ├── ⭐ Featured Products section
    ├── 🔥 Trending Now section
    └── 🛍️ All Products grid
         ↓
🖼️ Product Detail Screen
    ├── Full image (tap to zoom)
    ├── Category + Condition badges
    ├── Price with discount
    ├── View count + Enquiry count
    ├── Description
    ├── Meet the Artisan section
    ├── Similar Products
    ├── ⭐ Reviews button
    ├── 💬 WhatsApp button
    ├── 📞 Call Seller button
    └── 🚩 Report button
         ↓
🧑‍🎨 Artisan Profile
    ├── Total / Sold / Available stats
    └── All products by artisan
         ↓
➕ Add Product (Artisan)
    ├── Photo upload
    ├── Product details form
    ├── Category & Condition
    ├── Location
    └── 4-digit PIN setup
         ↓
👤 My Products (PIN Protected)
    ├── ✏️ Edit product
    ├── ✅ Mark as sold
    ├── 🗑️ Delete product
    └── 📊 Sales history
```

---

## 🎨 Color Theme

```
Kumkum Red      → #C0392B  (Headers, primary buttons)
Turmeric Yellow → #F5A623  (Price, highlights)
Cream White     → #FFF8E7  (Background)
Santhe Brown    → #795548  (Text labels)
Leaf Green      → #27AE60  (Available badge, delivery)
WhatsApp Green  → #25D366  (Message button)
```

---

## ✅ Success Criteria (Internship Requirements)

| Requirement | Status |
|-------------|--------|
| Clicking product opens detail with clear CTA | ✅ Done |
| Search functionality for product names | ✅ Done |
| Empty state handled gracefully | ✅ Done |
| Colorful UI reflecting Indian market | ✅ Done |
| Artisan upload (Name, Price, Photo) | ✅ Done |
| Grid view by category | ✅ Done |
| Mock message to seller (WhatsApp + Call) | ✅ Done |
| Image compression implemented | ✅ Done |

---

## 🌐 Multi-Language Support

| Language | Code | App Name |
|----------|------|----------|
| English (Default) | `en` | Artisan |
| Kannada | `kn` | ಕಲಾವಿದ |
| Hindi | `hi` | कारीगर |

Switch language anytime using the 🌐 button on the home screen.

---

## 📊 Impact Goals

| Goal | How |
|------|-----|
| 🌱 Economic Growth | Artisans reach urban buyers directly |
| 🎨 Preserving Crafts | 14 specific art categories |
| 📱 Digital Commerce | Physical → Phygital marketplace |
| 🔐 Trust & Safety | 4-digit PIN protection |
| 📍 Local Discovery | Location tags on all products |
| 🌐 Accessibility | 3-language support |

---

## 🚀 Future Scope

- [ ] OTP-based login with Firebase Authentication
- [ ] Google Maps integration for artisan location
- [ ] Video upload to showcase craft-making process
- [ ] In-app chat between buyer and artisan
- [ ] Payment gateway (Razorpay / UPI)
- [ ] Delivery tracking system
- [ ] AI-powered product recommendations
- [ ] Admin dashboard for platform management
- [ ] Push notifications for enquiries
- [ ] Offline mode with cached products

---

## 👨‍💻 Developer

**Vishwas** — Android Internship Project 2026
**GitHub:** [synvishwas](https://github.com/synvishwas)
**Project:** Android App Development using GenAI — MindMatrix Industry Readiness Programme

---

## 📄 License

This project is built for educational and internship purposes under the MindMatrix Industry Readiness Programme.

---

<p align="center">
  Made with ❤️ for Karnataka's Village Artisans 🏺🎨🧵
  <br/>
  <b>#VocalForLocal #HalliSanthe #MadeInKarnataka</b>
</p>
