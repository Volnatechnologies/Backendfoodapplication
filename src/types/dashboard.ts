export interface NavItem {
  id: string;
  label: string;
  iconName: string;
  badge?: string | number;
}

export interface FAQItem {
  id: string;
  question: string;
  answer: string;
  category?: string;
  helpfulCount?: { yes: number; no: number };
}

export interface ResourceCard {
  id: string;
  category: string;
  title: string;
  description: string;
  bgGradient: string;
  imageUrl?: string;
}

export interface CategoryCardData {
  id: string;
  title: string;
  description: string;
  ctaText: string;
  iconType: 'services' | 'earnings' | 'troubleshooting';
  bgColor: string;
}

export interface NotificationItem {
  id: string;
  title: string;
  message: string;
  time: string;
  unread: boolean;
  type: 'info' | 'alert' | 'success';
}

export interface ChatMessage {
  id: string;
  sender: 'user' | 'agent';
  text: string;
  timestamp: string;
}
