export const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export interface Category {
  id: number;
  name: string;
  description?: string;
  isActive: boolean;
  sortOrder: number;
}

export interface MenuItem {
  id: number;
  name: string;
  description?: string;
  internalCode?: string;
  price: number;
  discountedPrice?: number;
  protein?: number;
  carbohydrates?: number;
  fats?: number;
  categoryId: number;
  categoryName?: string;
  imageUrl?: string;
  calories?: number;
  dietaryTags: string[];
  allergens: string[];
  stockQuantity: number;
  status: 'IN_STOCK' | 'LOW_STOCK' | 'OUT_OF_STOCK';
  spicyLevel: 'NONE' | 'MILD' | 'MEDIUM' | 'HOT' | 'EXTRA_HOT';
  isVisible: boolean;
  isAvailable: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface PaginatedResponse<T> {
  success: boolean;
  data: T[];
  page?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
  message?: string;
  errorCode?: string;
}

export interface SingleResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  errorCode?: string;
}

// Category API Services
export const fetchCategories = async (): Promise<Category[]> => {
  const response = await fetch(`${BASE_URL}/api/menu/categories`);
  if (!response.ok) {
    throw new Error(`Failed to fetch categories: ${response.statusText}`);
  }
  const result: SingleResponse<Category[]> = await response.json();
  return result.data || [];
};

export const createCategory = async (data: { name: string; description?: string; sortOrder?: number }): Promise<Category> => {
  const response = await fetch(`${BASE_URL}/api/menu/categories`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  const result = await response.json();
  if (!response.ok || !result.success) {
    throw new Error(result.message || 'Failed to create category');
  }
  return result.data;
};

export const updateCategory = async (id: number, data: { name: string; description?: string; sortOrder?: number }): Promise<Category> => {
  const response = await fetch(`${BASE_URL}/api/menu/categories/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  const result = await response.json();
  if (!response.ok || !result.success) {
    throw new Error(result.message || 'Failed to update category');
  }
  return result.data;
};

export const deleteCategory = async (id: number): Promise<void> => {
  const response = await fetch(`${BASE_URL}/api/menu/categories/${id}`, {
    method: 'DELETE',
  });
  if (!response.ok && response.status !== 204) {
    const result = await response.json().catch(() => ({}));
    throw new Error(result.message || 'Failed to delete category');
  }
};

// Menu Item API Services
export const fetchMenuItems = async (params?: {
  search?: string;
  categoryId?: number;
  status?: string;
  visible?: boolean;
  available?: boolean;
  page?: number;
  size?: number;
}): Promise<PaginatedResponse<MenuItem>> => {
  const query = new URLSearchParams();
  if (params?.search) query.append('search', params.search);
  if (params?.categoryId) query.append('categoryId', params.categoryId.toString());
  if (params?.status) query.append('status', params.status);
  if (params?.visible !== undefined) query.append('visible', params.visible.toString());
  if (params?.available !== undefined) query.append('available', params.available.toString());
  if (params?.page !== undefined) query.append('page', params.page.toString());
  if (params?.size !== undefined) query.append('size', params.size.toString());

  const url = `${BASE_URL}/api/menu${query.toString() ? `?${query.toString()}` : ''}`;
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Failed to fetch menu items: ${response.statusText}`);
  }
  return await response.json();
};

export const createMenuItem = async (itemData: any, imageFile?: File): Promise<MenuItem> => {
  const formData = new FormData();
  formData.append('item', new Blob([JSON.stringify(itemData)], { type: 'application/json' }));
  if (imageFile) {
    formData.append('image', imageFile);
  }

  const response = await fetch(`${BASE_URL}/api/menu`, {
    method: 'POST',
    body: formData,
  });
  const result = await response.json();
  if (!response.ok || !result.success) {
    throw new Error(result.message || 'Failed to create menu item');
  }
  return result.data;
};

export const updateMenuItem = async (id: number, itemData: any, imageFile?: File): Promise<MenuItem> => {
  const formData = new FormData();
  formData.append('item', new Blob([JSON.stringify(itemData)], { type: 'application/json' }));
  if (imageFile) {
    formData.append('image', imageFile);
  }

  const response = await fetch(`${BASE_URL}/api/menu/${id}`, {
    method: 'PUT',
    body: formData,
  });
  const result = await response.json();
  if (!response.ok || !result.success) {
    throw new Error(result.message || 'Failed to update menu item');
  }
  return result.data;
};

export const updateStock = async (id: number, stockQuantity: number): Promise<MenuItem> => {
  const response = await fetch(`${BASE_URL}/api/menu/${id}/stock`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ stockQuantity }),
  });
  const result = await response.json();
  if (!response.ok || !result.success) {
    throw new Error(result.message || 'Failed to update stock');
  }
  return result.data;
};

export const updateVisibility = async (id: number, isVisible: boolean): Promise<MenuItem> => {
  const response = await fetch(`${BASE_URL}/api/menu/${id}/visibility`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ isVisible }),
  });
  const result = await response.json();
  if (!response.ok || !result.success) {
    throw new Error(result.message || 'Failed to update visibility');
  }
  return result.data;
};

export const updateStatus = async (id: number, status: string): Promise<MenuItem> => {
  const response = await fetch(`${BASE_URL}/api/menu/${id}/status`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ status }),
  });
  const result = await response.json();
  if (!response.ok || !result.success) {
    throw new Error(result.message || 'Failed to update status');
  }
  return result.data;
};

export const updateAvailability = async (id: number, available: boolean): Promise<MenuItem> => {
  const response = await fetch(`${BASE_URL}/api/menu/${id}/availability?available=${available}`, {
    method: 'PATCH',
  });
  const result = await response.json();
  if (!response.ok || !result.success) {
    throw new Error(result.message || 'Failed to update availability');
  }
  return result.data;
};

export const deleteMenuItem = async (id: number): Promise<void> => {
  const response = await fetch(`${BASE_URL}/api/menu/${id}`, {
    method: 'DELETE',
  });
  if (!response.ok && response.status !== 204) {
    const result = await response.json().catch(() => ({}));
    throw new Error(result.message || 'Failed to delete menu item');
  }
};
