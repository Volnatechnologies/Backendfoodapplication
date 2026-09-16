package com.caloryhive.business.config;

import com.caloryhive.business.analytics.entity.Order;
import com.caloryhive.business.analytics.entity.OrderItem;
import com.caloryhive.business.analytics.repository.OrderItemRepository;
import com.caloryhive.business.analytics.repository.OrderRepository;
import com.caloryhive.business.business.entity.Business;
import com.caloryhive.business.business.repository.BusinessRepository;
import com.caloryhive.business.finance.entity.BankAccount;
import com.caloryhive.business.finance.entity.BusinessFinancialAccount;
import com.caloryhive.business.finance.entity.FinancialTransaction;
import com.caloryhive.business.finance.repository.BankAccountRepository;
import com.caloryhive.business.finance.repository.BusinessFinancialAccountRepository;
import com.caloryhive.business.finance.repository.FinancialTransactionRepository;
import com.caloryhive.business.reviews.entity.Review;
import com.caloryhive.business.reviews.entity.ReviewPhoto;
import com.caloryhive.business.reviews.entity.ReviewResponse;
import com.caloryhive.business.reviews.entity.ReviewSentiment;
import com.caloryhive.business.reviews.repository.ReviewRepository;
import com.caloryhive.business.reviews.repository.ReviewResponseRepository;
import com.caloryhive.business.rewards.entity.BusinessObjectiveProgress;
import com.caloryhive.business.rewards.entity.BusinessRewardAccount;
import com.caloryhive.business.rewards.entity.RewardCatalogItem;
import com.caloryhive.business.rewards.entity.RewardObjective;
import com.caloryhive.business.rewards.entity.RewardPerk;
import com.caloryhive.business.rewards.entity.RewardTier;
import com.caloryhive.business.rewards.repository.BusinessObjectiveProgressRepository;
import com.caloryhive.business.rewards.repository.BusinessRewardAccountRepository;
import com.caloryhive.business.rewards.repository.RewardCatalogItemRepository;
import com.caloryhive.business.rewards.repository.RewardObjectiveRepository;
import com.caloryhive.business.rewards.repository.RewardPerkRepository;
import com.caloryhive.business.rewards.repository.RewardTierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BusinessRepository businessRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewResponseRepository reviewResponseRepository;
    private final BusinessFinancialAccountRepository financialAccountRepository;
    private final BankAccountRepository bankAccountRepository;
    private final FinancialTransactionRepository financialTransactionRepository;
    private final RewardTierRepository rewardTierRepository;
    private final BusinessRewardAccountRepository businessRewardAccountRepository;
    private final RewardPerkRepository rewardPerkRepository;
    private final RewardObjectiveRepository rewardObjectiveRepository;
    private final BusinessObjectiveProgressRepository objectiveProgressRepository;
    private final RewardCatalogItemRepository rewardCatalogItemRepository;

    public static final UUID DEMO_BUSINESS_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final UUID DEMO_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Override
    @Transactional
    public void run(String... args) {
        if (businessRepository.count() > 0) {
            log.info("Database already initialized with business records. Skipping initial seeding.");
            return;
        }

        log.info("Initializing demo database with UI reference data for all 4 screens...");

        // 1. Demo Business
        businessRepository.save(Business.builder()
                .id(DEMO_BUSINESS_ID)
                .ownerId(DEMO_USER_ID)
                .name("Calorye Hive Central")
                .description("Fine Casual Healthy Dining & Artisan Kitchen")
                .phone("+1 (555) 234-5678")
                .email("owner@caloryehive.com")
                .address("100 Market Street, Suite 400, San Francisco, CA 94105")
                .cuisineType("Artisan Healthy / Fusion")
                .status("ACTIVE")
                .build());

        // 2. Seed Orders matching UI
        Order o1 = orderRepository.save(Order.builder()
                .id(UUID.fromString("a0000001-0000-0000-0000-000000000001"))
                .orderNumber("ORD-9021")
                .businessId(DEMO_BUSINESS_ID)
                .customerName("Eleanor V.")
                .customerPhone("+1-555-0101")
                .orderType("DINE_IN")
                .totalAmount(new BigDecimal("165.00"))
                .status("COMPLETED")
                .paymentStatus("PAID")
                .satisfactionRating(new BigDecimal("5.0"))
                .fulfillmentTimeMinutes(18)
                .build());

        Order o2 = orderRepository.save(Order.builder()
                .id(UUID.fromString("a0000001-0000-0000-0000-000000000002"))
                .orderNumber("ORD-9020")
                .businessId(DEMO_BUSINESS_ID)
                .customerName("Marcus T.")
                .customerPhone("+1-555-0102")
                .orderType("DELIVERY")
                .deliveryAddress("742 Evergreen Terrace")
                .totalAmount(new BigDecimal("64.50"))
                .status("PENDING")
                .paymentStatus("PENDING")
                .satisfactionRating(new BigDecimal("4.5"))
                .fulfillmentTimeMinutes(25)
                .build());

        Order o3 = orderRepository.save(Order.builder()
                .id(UUID.fromString("a0000001-0000-0000-0000-000000000003"))
                .orderNumber("ORD-9019")
                .businessId(DEMO_BUSINESS_ID)
                .customerName("Sophia L.")
                .customerPhone("+1-555-0103")
                .orderType("DINE_IN")
                .totalAmount(new BigDecimal("210.00"))
                .status("COMPLETED")
                .paymentStatus("PAID")
                .satisfactionRating(new BigDecimal("5.0"))
                .fulfillmentTimeMinutes(20)
                .build());

        Order o4 = orderRepository.save(Order.builder()
                .id(UUID.fromString("a0000001-0000-0000-0000-000000000004"))
                .orderNumber("ORD-9018")
                .businessId(DEMO_BUSINESS_ID)
                .customerName("David K.")
                .customerPhone("+1-555-0104")
                .orderType("DINE_IN")
                .totalAmount(new BigDecimal("145.20"))
                .status("COMPLETED")
                .paymentStatus("PAID")
                .satisfactionRating(new BigDecimal("4.8"))
                .fulfillmentTimeMinutes(22)
                .build());

        Order o5 = orderRepository.save(Order.builder()
                .id(UUID.fromString("a0000001-0000-0000-0000-000000000005"))
                .orderNumber("ORD-9017")
                .businessId(DEMO_BUSINESS_ID)
                .customerName("Rachel Green")
                .customerPhone("+1-555-0105")
                .orderType("CATERING")
                .deliveryAddress("Corporate Plaza #4")
                .totalAmount(new BigDecimal("580.00"))
                .status("COMPLETED")
                .paymentStatus("PAID")
                .satisfactionRating(new BigDecimal("5.0"))
                .fulfillmentTimeMinutes(45)
                .build());

        Order o6 = orderRepository.save(Order.builder()
                .id(UUID.fromString("a0000001-0000-0000-0000-000000000006"))
                .orderNumber("ORD-9016")
                .businessId(DEMO_BUSINESS_ID)
                .customerName("James Wilson")
                .customerPhone("+1-555-0106")
                .orderType("DELIVERY")
                .deliveryAddress("124 Conch St")
                .totalAmount(new BigDecimal("82.50"))
                .status("COMPLETED")
                .paymentStatus("PAID")
                .satisfactionRating(new BigDecimal("4.7"))
                .fulfillmentTimeMinutes(24)
                .build());

        Order o7 = orderRepository.save(Order.builder()
                .id(UUID.fromString("a0000001-0000-0000-0000-000000000007"))
                .orderNumber("ORD-9015")
                .businessId(DEMO_BUSINESS_ID)
                .customerName("Amelia Earhart")
                .customerPhone("+1-555-0107")
                .orderType("DINE_IN")
                .totalAmount(new BigDecimal("95.00"))
                .status("COMPLETED")
                .paymentStatus("PAID")
                .satisfactionRating(new BigDecimal("4.9"))
                .fulfillmentTimeMinutes(19)
                .build());

        // Order Items
        orderItemRepository.save(OrderItem.builder()
                .id(UUID.fromString("b0000001-0000-0000-0000-000000000001"))
                .order(o1)
                .menuItemName("Truffle Mushroom Burger")
                .categoryName("Main Course")
                .quantity(342)
                .price(new BigDecimal("16.00"))
                .imageUrl("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=100")
                .build());

        orderItemRepository.save(OrderItem.builder()
                .id(UUID.fromString("b0000001-0000-0000-0000-000000000002"))
                .order(o2)
                .menuItemName("Harvest Quinoa Bowl")
                .categoryName("Salads")
                .quantity(289)
                .price(new BigDecimal("12.00"))
                .imageUrl("https://images.unsplash.com/photo-1540420773420-3366772f4999?w=100")
                .build());

        orderItemRepository.save(OrderItem.builder()
                .id(UUID.fromString("b0000001-0000-0000-0000-000000000003"))
                .order(o3)
                .menuItemName("Sweet Potato Fries")
                .categoryName("Sides")
                .quantity(416)
                .price(new BigDecimal("7.00"))
                .imageUrl("https://images.unsplash.com/photo-1576107232684-1279f3908594?w=100")
                .build());

        // 3. Reviews
        Review r1 = Review.builder()
                .id(UUID.fromString("c0000001-0000-0000-0000-000000000001"))
                .businessId(DEMO_BUSINESS_ID)
                .customerName("Victoria Sterling")
                .customerAvatar("https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100")
                .rating(5)
                .comment("The Truffle Mushroom Burger is the best burger I have ever tasted! Extraordinary truffle aroma and perfectly toasted brioche.")
                .sentiment(ReviewSentiment.POSITIVE)
                .sentimentScore(new BigDecimal("98.00"))
                .answered(true)
                .hasPhotos(true)
                .orderReference("ORD-9021")
                .photos(new ArrayList<>())
                .build();
        r1.getPhotos().add(ReviewPhoto.builder()
                .id(UUID.fromString("d0000001-0000-0000-0000-000000000001"))
                .review(r1)
                .photoUrl("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600")
                .build());
        reviewRepository.save(r1);

        Review r2 = Review.builder()
                .id(UUID.fromString("c0000001-0000-0000-0000-000000000002"))
                .businessId(DEMO_BUSINESS_ID)
                .customerName("Liam Henderson")
                .customerAvatar("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100")
                .rating(5)
                .comment("Exceptional catering service for our corporate luncheon. Arrived exactly on time and hot.")
                .sentiment(ReviewSentiment.POSITIVE)
                .sentimentScore(new BigDecimal("95.00"))
                .answered(true)
                .hasPhotos(false)
                .orderReference("ORD-9017")
                .photos(new ArrayList<>())
                .build();
        reviewRepository.save(r2);

        Review r3 = Review.builder()
                .id(UUID.fromString("c0000001-0000-0000-0000-000000000003"))
                .businessId(DEMO_BUSINESS_ID)
                .customerName("Chloe Zhao")
                .customerAvatar("https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100")
                .rating(4)
                .comment("Very fresh ingredients in the Quinoa bowl, loved the citrus vinaigrette dressing!")
                .sentiment(ReviewSentiment.POSITIVE)
                .sentimentScore(new BigDecimal("88.00"))
                .answered(false)
                .hasPhotos(true)
                .orderReference("ORD-9019")
                .photos(new ArrayList<>())
                .build();
        r3.getPhotos().add(ReviewPhoto.builder()
                .id(UUID.fromString("d0000001-0000-0000-0000-000000000002"))
                .review(r3)
                .photoUrl("https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600")
                .build());
        reviewRepository.save(r3);

        Review r4 = Review.builder()
                .id(UUID.fromString("c0000001-0000-0000-0000-000000000004"))
                .businessId(DEMO_BUSINESS_ID)
                .customerName("Brandon Cole")
                .customerAvatar("https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100")
                .rating(2)
                .comment("Delivery arrived 15 minutes late and fries were a bit lukewarm.")
                .sentiment(ReviewSentiment.NEGATIVE)
                .sentimentScore(new BigDecimal("25.00"))
                .answered(false)
                .hasPhotos(false)
                .orderReference("ORD-9020")
                .photos(new ArrayList<>())
                .build();
        reviewRepository.save(r4);

        reviewResponseRepository.save(ReviewResponse.builder()
                .id(UUID.fromString("e0000001-0000-0000-0000-000000000001"))
                .review(r1)
                .businessId(DEMO_BUSINESS_ID)
                .responderId(DEMO_USER_ID)
                .responderName("Sarah Jenkins (General Manager)")
                .responseText("Thank you so much Victoria! Our culinary team puts immense love into our truffle glaze. Looking forward to welcoming you back soon!")
                .build());

        reviewResponseRepository.save(ReviewResponse.builder()
                .id(UUID.fromString("e0000001-0000-0000-0000-000000000002"))
                .review(r2)
                .businessId(DEMO_BUSINESS_ID)
                .responderId(DEMO_USER_ID)
                .responderName("Sarah Jenkins (General Manager)")
                .responseText("Liam, it was our absolute pleasure catering for your corporate group! Delighted to hear everything was on point.")
                .build());

        // 4. Financial Account & Bank Accounts
        financialAccountRepository.save(BusinessFinancialAccount.builder()
                .id(UUID.fromString("f0000001-0000-0000-0000-000000000001"))
                .businessId(DEMO_BUSINESS_ID)
                .availableBalance(new BigDecimal("14285.50"))
                .pendingBalance(new BigDecimal("3412.00"))
                .monthlyGoal(new BigDecimal("42500.00"))
                .monthlyGoalAchieved(new BigDecimal("42500.00"))
                .currency("USD")
                .nextPayoutDate(LocalDate.now().plusDays(2))
                .verifiedBy("STRIPE_CONNECT")
                .build());

        bankAccountRepository.save(BankAccount.builder()
                .id(UUID.fromString("f1000001-0000-0000-0000-000000000001"))
                .businessId(DEMO_BUSINESS_ID)
                .bankName("Chase Business Premier")
                .accountHolderName("Calorye Hive Central LLC")
                .maskedAccountNumber("**** 4210")
                .routingNumber("121000358")
                .accountType("Checking Account")
                .isDefault(true)
                .isVerified(true)
                .build());

        bankAccountRepository.save(BankAccount.builder()
                .id(UUID.fromString("f1000001-0000-0000-0000-000000000002"))
                .businessId(DEMO_BUSINESS_ID)
                .bankName("Wells Fargo Commercial")
                .accountHolderName("Calorye Hive Central LLC")
                .maskedAccountNumber("**** 8829")
                .routingNumber("121000248")
                .accountType("Savings Account")
                .isDefault(false)
                .isVerified(true)
                .build());

        financialTransactionRepository.save(FinancialTransaction.builder()
                .id(UUID.fromString("f2000001-0000-0000-0000-000000000001"))
                .businessId(DEMO_BUSINESS_ID)
                .type("WITHDRAWAL")
                .amount(new BigDecimal("4500.00"))
                .status("COMPLETED")
                .description("Automatic Payout to Chase Business **** 4210")
                .referenceId("TRX-99812")
                .build());

        financialTransactionRepository.save(FinancialTransaction.builder()
                .id(UUID.fromString("f2000001-0000-0000-0000-000000000002"))
                .businessId(DEMO_BUSINESS_ID)
                .type("DAILY_SALES")
                .amount(new BigDecimal("1820.40"))
                .status("COMPLETED")
                .description("Daily POS Settlement - 48 Orders")
                .referenceId("TRX-99811")
                .build());

        // 5. Partnership Rewards
        RewardTier bronze = rewardTierRepository.save(RewardTier.builder()
                .id(UUID.fromString("70000001-0000-0000-0000-000000000001"))
                .tierName("BRONZE")
                .displayName("Bronze Partner")
                .minPoints(0)
                .nextTierName("SILVER")
                .nextTierPoints(2500)
                .commissionDiscountPercent(BigDecimal.ZERO)
                .tagline("Entry-level partner benefits")
                .build());

        RewardTier silver = rewardTierRepository.save(RewardTier.builder()
                .id(UUID.fromString("70000001-0000-0000-0000-000000000002"))
                .tierName("SILVER")
                .displayName("Silver Partner")
                .minPoints(2500)
                .nextTierName("GOLD")
                .nextTierPoints(5000)
                .commissionDiscountPercent(new BigDecimal("1.25"))
                .tagline("Standard growth partner tier")
                .build());

        RewardTier gold = rewardTierRepository.save(RewardTier.builder()
                .id(UUID.fromString("70000001-0000-0000-0000-000000000003"))
                .tierName("GOLD")
                .displayName("Gold Partner")
                .minPoints(5000)
                .nextTierName("PLATINUM")
                .nextTierPoints(10000)
                .commissionDiscountPercent(new BigDecimal("2.50"))
                .tagline("High-volume premium partner tier")
                .build());

        RewardTier platinum = rewardTierRepository.save(RewardTier.builder()
                .id(UUID.fromString("70000001-0000-0000-0000-000000000004"))
                .tierName("PLATINUM")
                .displayName("Platinum Partner")
                .minPoints(10000)
                .nextTierName("DIAMOND")
                .nextTierPoints(15000)
                .commissionDiscountPercent(new BigDecimal("5.00"))
                .tagline("VIP partner status with dedicated support")
                .build());

        RewardTier diamond = rewardTierRepository.save(RewardTier.builder()
                .id(UUID.fromString("70000001-0000-0000-0000-000000000005"))
                .tierName("DIAMOND")
                .displayName("Diamond Partner")
                .minPoints(15000)
                .nextTierName(null)
                .nextTierPoints(null)
                .commissionDiscountPercent(new BigDecimal("8.00"))
                .tagline("Elite tier with zero commission fee days")
                .build());

        businessRewardAccountRepository.save(BusinessRewardAccount.builder()
                .id(UUID.fromString("71000001-0000-0000-0000-000000000001"))
                .businessId(DEMO_BUSINESS_ID)
                .currentTier(platinum)
                .totalPointsEarned(18500)
                .currentPointsBalance(12450)
                .build());

        rewardPerkRepository.save(RewardPerk.builder()
                .id(UUID.fromString("72000001-0000-0000-0000-000000000001"))
                .tier(platinum)
                .title("24/7 Dedicated Priority Support")
                .description("Direct VIP hotline to our senior restaurant success team with < 5 min response.")
                .iconName("Headphones")
                .isUpcoming(false)
                .sortOrder(1)
                .build());

        rewardPerkRepository.save(RewardPerk.builder()
                .id(UUID.fromString("72000001-0000-0000-0000-000000000002"))
                .tier(platinum)
                .title("Advanced Predictive Analytics")
                .description("AI-powered item velocity forecasts, demand curves, and heat maps.")
                .iconName("LineChart")
                .isUpcoming(false)
                .sortOrder(2)
                .build());

        rewardPerkRepository.save(RewardPerk.builder()
                .id(UUID.fromString("72000001-0000-0000-0000-000000000003"))
                .tier(platinum)
                .title("Featured Top-Tier Verified Badge")
                .description("Gold verification checkmark on customer discovery search & menu.")
                .iconName("BadgeCheck")
                .isUpcoming(false)
                .sortOrder(3)
                .build());

        rewardPerkRepository.save(RewardPerk.builder()
                .id(UUID.fromString("72000001-0000-0000-0000-000000000004"))
                .tier(diamond)
                .title("0% Commission Weekend Days (Diamond Only)")
                .description("Keep 100% of order totals on all app orders every last weekend of the month.")
                .iconName("Sparkles")
                .isUpcoming(true)
                .sortOrder(4)
                .build());

        RewardObjective obj1 = rewardObjectiveRepository.save(RewardObjective.builder()
                .id(UUID.fromString("73000001-0000-0000-0000-000000000001"))
                .title("Maintain 4.8 Rating")
                .description("Keep average customer rating at or above 4.8 stars for 30 consecutive days.")
                .pointsReward(1500)
                .targetType("RATING")
                .targetValue(new BigDecimal("30.00"))
                .unit("days")
                .sortOrder(1)
                .build());

        RewardObjective obj2 = rewardObjectiveRepository.save(RewardObjective.builder()
                .id(UUID.fromString("73000001-0000-0000-0000-000000000002"))
                .title("Volume Excellence (1,500 Orders)")
                .description("Fulfill 1,500 total orders within this current calendar quarter.")
                .pointsReward(3000)
                .targetType("VOLUME")
                .targetValue(new BigDecimal("1500.00"))
                .unit("orders")
                .sortOrder(2)
                .build());

        objectiveProgressRepository.save(BusinessObjectiveProgress.builder()
                .id(UUID.fromString("74000001-0000-0000-0000-000000000001"))
                .businessId(DEMO_BUSINESS_ID)
                .objective(obj1)
                .currentValue(new BigDecimal("20.00"))
                .targetValue(new BigDecimal("30.00"))
                .progressPercentage(66)
                .progressLabel("20 of 30 days completed")
                .isCompleted(false)
                .build());

        objectiveProgressRepository.save(BusinessObjectiveProgress.builder()
                .id(UUID.fromString("74000001-0000-0000-0000-000000000002"))
                .businessId(DEMO_BUSINESS_ID)
                .objective(obj2)
                .currentValue(new BigDecimal("1020.00"))
                .targetValue(new BigDecimal("1500.00"))
                .progressPercentage(68)
                .progressLabel("1,020 of 1,500 orders fulfilled")
                .isCompleted(false)
                .build());

        rewardCatalogItemRepository.save(RewardCatalogItem.builder()
                .id(UUID.fromString("75000001-0000-0000-0000-000000000001"))
                .title("Professional Food Photoshoot")
                .description("On-site professional food photography for up to 15 signature menu items.")
                .pointsCost(5000)
                .category("MARKETING")
                .imageUrl("https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=500")
                .isActive(true)
                .build());

        rewardCatalogItemRepository.save(RewardCatalogItem.builder()
                .id(UUID.fromString("75000001-0000-0000-0000-000000000002"))
                .title("1-Week App Hero Banner Placement")
                .description("Guaranteed placement on the app home carousel reaching 50,000+ local diners.")
                .pointsCost(8500)
                .category("PROMOTION")
                .imageUrl("https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=500")
                .isActive(true)
                .build());

        log.info("Data initialization complete! Demo business, orders, reviews, finance, and rewards seeded.");
    }
}