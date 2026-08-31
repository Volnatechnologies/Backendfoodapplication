import type { NavItem, FAQItem, ResourceCard, CategoryCardData, NotificationItem } from '../types/dashboard';

export const navigationItems: NavItem[] = [
  { id: 'dashboard', label: 'Dashboard', iconName: 'LayoutGrid' },
  { id: 'bookings', label: 'Bookings', iconName: 'Calendar' },
  { id: 'analytics', label: 'Analytics', iconName: 'LineChart' },
  { id: 'services', label: 'Services', iconName: 'SlidersHorizontal' },
  { id: 'performance', label: 'Performance', iconName: 'Activity' },
  { id: 'reviews', label: 'Reviews', iconName: 'MessageSquare' },
  { id: 'transactions', label: 'Transactions', iconName: 'CreditCard' },
  { id: 'profile', label: 'Profile', iconName: 'User' },
];

export const gettingStartedFaqs: FAQItem[] = [
  {
    id: 'gs-1',
    question: 'How do I create my primary Account?',
    answer: "To connect your data source, navigate to Settings > Integrations and click the 'Add Source' button. EchoManager supports direct connections to PostgreSQL, Snowflake, and BigQuery.\n\nOnce you've selected your provider, you'll need to enter your credentials and white-list our egress IP addresses in your firewall settings. The connection validation usually takes less than 30 seconds.",
    category: 'Getting Started',
    helpfulCount: { yes: 142, no: 8 }
  },
  {
    id: 'gs-2',
    question: 'Understanding the 14-day free trial limits',
    answer: 'During your 14-day free trial, you have full access to all Professional Suite features including multi-seat collaboration, automated scheduling, and advanced API routing. Trial accounts are capped at 1,000 monthly API calls and 5 team member invites. Upgrading to a paid plan unlocks unlimited throughput.',
    category: 'Getting Started',
    helpfulCount: { yes: 98, no: 3 }
  },
  {
    id: 'gs-3',
    question: 'How to reset your account?',
    answer: 'To reset your account configuration or clear test data, go to Profile > Account Settings > Danger Zone. Click "Reset Account Workspace". Note that this will erase custom integrations, active session logs, and saved view presets. We recommend exporting your audit logs before initiating a reset.',
    category: 'Getting Started',
    helpfulCount: { yes: 64, no: 12 }
  }
];

export const featuredResources: ResourceCard[] = [
  {
    id: 'res-1',
    category: 'TECHNICAL GUIDE',
    title: 'Advanced API Routing',
    description: 'Learn how to optimize your webhook delivery by 40%.',
    bgGradient: 'from-blue-600/90 to-sky-900/90',
    imageUrl: 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?auto=format&fit=crop&w=800&q=80'
  },
  {
    id: 'res-2',
    category: 'BEST PRACTICE',
    title: 'Collaborative Workflows',
    description: 'Managing large-scale teams across multiple timezones.',
    bgGradient: 'from-sky-600/90 to-slate-900/90',
    imageUrl: 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?auto=format&fit=crop&w=800&q=80'
  }
];

export const categoryCards: CategoryCardData[] = [
  {
    id: 'cat-1',
    title: 'Managing Your Services',
    description: 'Learn how to update items, set seasonal prices, and manage dietary tags effortlessly.',
    ctaText: 'Explore Guide',
    iconType: 'services',
    bgColor: 'bg-black text-white'
  },
  {
    id: 'cat-2',
    title: 'Earnings & Payouts',
    description: 'Track your weekly performance, understand commissions, and manage your banking details.',
    ctaText: 'Track Revenue',
    iconType: 'earnings',
    bgColor: 'bg-blue-600 text-white'
  },
  {
    id: 'cat-3',
    title: 'App Troubleshooting',
    description: 'Quick fixes for printer connection, tablet synchronization, and notification alerts.',
    ctaText: 'Get Tech Support',
    iconType: 'troubleshooting',
    bgColor: 'bg-rose-500 text-white'
  }
];

export const generalFaqs: FAQItem[] = [
  {
    id: 'faq-1',
    question: 'How do I update store hours?',
    answer: 'Navigate to Services > Operating Hours in your dashboard. You can define standard weekly schedules, set holiday overrides, or enable temporary pause modes during peak rush hours.'
  },
  {
    id: 'faq-2',
    question: 'When will I receive my payout?',
    answer: 'Payouts are processed automatically on a weekly rolling basis every Monday morning at 00:00 UTC. Standard bank processing times take 1-3 business days depending on your financial institution.'
  },
  {
    id: 'faq-3',
    question: 'Can I manage multiple locations?',
    answer: 'Yes! Multi-location support is built directly into the Professional Suite. Use the location switcher at the top left of your screen to toggle between stores or manage aggregated analytics.'
  },
  {
    id: 'faq-4',
    question: 'What happens if an order is cancelled?',
    answer: 'When an order is cancelled prior to dispatch, funds are automatically refunded to the customer. Your merchant ledger will reflect the adjustment within 15 minutes, with zero processing penalties.'
  }
];

export const mockNotifications: NotificationItem[] = [
  {
    id: 'n-1',
    title: 'Weekly Payout Processed',
    message: 'Your payout of $4,850.00 has been transferred to your connected bank account.',
    time: '10 mins ago',
    unread: true,
    type: 'success'
  },
  {
    id: 'n-2',
    title: 'System Maintenance Notice',
    message: 'Scheduled API gateway optimization on Sunday 02:00 UTC (15 mins expected downtime).',
    time: '2 hours ago',
    unread: true,
    type: 'info'
  },
  {
    id: 'n-3',
    title: 'Tablet Sync Warning',
    message: 'Location #3 tablet reported high latency. Restart app to resync menu updates.',
    time: '1 day ago',
    unread: false,
    type: 'alert'
  }
];
