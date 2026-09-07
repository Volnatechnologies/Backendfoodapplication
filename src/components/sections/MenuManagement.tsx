import React, { useState, useEffect } from 'react';
import {
  Search,
  Filter,
  Plus,
  ListFilter,
  Clock,
  Grid,
  Pencil,
  Flame,
  X,
  Loader2,
  AlertCircle,
  Trash2,
  CheckCircle2
} from 'lucide-react';
import {
  fetchCategories,
  fetchMenuItems,
  updateStock,
  updateVisibility,
  deleteMenuItem,
  createCategory,
} from '../../services/api';
import type { Category, MenuItem } from '../../services/api';
import { AddNewItemModal } from '../modals/AddNewItemModal';

export const MenuManagement: React.FC = () => {
  // State from Backend APIs
  const [categories, setCategories] = useState<Category[]>([]);
  const [items, setItems] = useState<MenuItem[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  // Filters State
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | null>(null);

  // Modal States
  const [isItemModalOpen, setIsItemModalOpen] = useState<boolean>(false);
  const [editingItem, setEditingItem] = useState<MenuItem | null>(null);
  const [isCategoryModalOpen, setIsCategoryModalOpen] = useState<boolean>(false);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  // Category Form State
  const [newCatName, setNewCatName] = useState('');
  const [newCatDesc, setNewCatDesc] = useState('');

  // Stock Quick Edit Modal State
  const [stockEditItem, setStockEditItem] = useState<MenuItem | null>(null);
  const [newStockVal, setNewStockVal] = useState<string>('');

  // Initial Load
  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [catsData, itemsResponse] = await Promise.all([
        fetchCategories(),
        fetchMenuItems({
          search: searchQuery,
          categoryId: selectedCategoryId || undefined,
        }),
      ]);
      setCategories(catsData);
      setItems(itemsResponse.data || []);
    } catch (err: any) {
      setError(err.message || 'Failed to load menu data from backend server');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [selectedCategoryId]);

  // Debounced search query trigger
  useEffect(() => {
    const timer = setTimeout(() => {
      loadData();
    }, 300);
    return () => clearTimeout(timer);
  }, [searchQuery]);

  // Auto-dismiss success notification
  useEffect(() => {
    if (successMsg) {
      const timer = setTimeout(() => setSuccessMsg(null), 4000);
      return () => clearTimeout(timer);
    }
  }, [successMsg]);

  // Open New / Edit Item Modal
  const openItemModal = (item?: MenuItem) => {
    setEditingItem(item || null);
    setIsItemModalOpen(true);
  };

  // Submit Category Form
  const handleCategorySubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newCatName.trim()) {
      setError('Category name cannot be empty');
      return;
    }

    setIsSubmitting(true);
    setError(null);
    try {
      const created = await createCategory({
        name: newCatName.trim(),
        description: newCatDesc,
        sortOrder: categories.length + 1,
      });
      setCategories((prev) => [...prev, created]);
      setSuccessMsg(`Category "${created.name}" created successfully`);
      setNewCatName('');
      setNewCatDesc('');
      setIsCategoryModalOpen(false);
    } catch (err: any) {
      setError(err.message || 'Failed to create category');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Toggle Item Visibility (Live / Hidden)
  const handleToggleVisibility = async (item: MenuItem) => {
    const newVis = !item.isVisible;
    try {
      const updated = await updateVisibility(item.id, newVis);
      setItems((prev) => prev.map((i) => (i.id === item.id ? updated : i)));
      setSuccessMsg(`"${item.name}" visibility changed to ${newVis ? 'Live' : 'Hidden'}`);
    } catch (err: any) {
      setError(err.message || 'Failed to update visibility');
    }
  };

  // Submit Stock Quick Update
  const handleStockUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!stockEditItem) return;
    const qty = parseInt(newStockVal, 10);
    if (isNaN(qty) || qty < 0) return;

    try {
      const updated = await updateStock(stockEditItem.id, qty);
      setItems((prev) => prev.map((i) => (i.id === stockEditItem.id ? updated : i)));
      setSuccessMsg(`Updated stock quantity for "${stockEditItem.name}" to ${qty}`);
      setStockEditItem(null);
    } catch (err: any) {
      setError(err.message || 'Failed to update stock');
    }
  };

  // Delete Confirmation State
  const [deleteConfirmItem, setDeleteConfirmItem] = useState<MenuItem | null>(null);
  const [isDeleting, setIsDeleting] = useState<boolean>(false);

  // Trigger Delete Confirmation Modal
  const handleDeleteClick = (item: MenuItem) => {
    setError(null);
    setDeleteConfirmItem(item);
  };

  // Confirm and Execute Delete
  const handleConfirmDelete = async () => {
    if (!deleteConfirmItem) return;
    const { id, name } = deleteConfirmItem;
    setIsDeleting(true);
    setError(null);
    try {
      await deleteMenuItem(id);
      setItems((prev) => prev.filter((i) => i.id !== id));
      setSuccessMsg(`"${name}" was deleted successfully`);
      setDeleteConfirmItem(null);
      loadData();
    } catch (err: any) {
      setError(err.message || 'Failed to delete menu item');
      setDeleteConfirmItem(null);
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Notifications / Banners */}
      {error && (
        <div className="bg-rose-50 border border-rose-200 text-rose-800 px-4 py-3 rounded-2xl flex items-center justify-between text-xs font-semibold shadow-sm">
          <div className="flex items-center space-x-2">
            <AlertCircle className="w-4 h-4 text-rose-600" />
            <span>{error}</span>
          </div>
          <button onClick={() => setError(null)} className="text-rose-500 hover:text-rose-800">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {successMsg && (
        <div className="bg-emerald-50 border border-emerald-200 text-emerald-800 px-4 py-3 rounded-2xl flex items-center justify-between text-xs font-semibold shadow-sm">
          <div className="flex items-center space-x-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-600" />
            <span>{successMsg}</span>
          </div>
          <button onClick={() => setSuccessMsg(null)} className="text-emerald-500 hover:text-emerald-800">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* TOP HEADER ROW */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-black text-slate-900 tracking-tight">Menu Management</h1>
          <p className="text-xs text-slate-500 font-medium">Organize, price, and update your culinary offerings.</p>
        </div>

        {/* Top Right Action Buttons */}
        <div className="flex items-center space-x-3">
          <button
            onClick={() => setIsCategoryModalOpen(true)}
            className="flex items-center space-x-2 px-4 py-2 bg-white border border-slate-200 rounded-full text-xs font-bold text-slate-700 hover:bg-slate-50 transition-all shadow-sm active:scale-95"
          >
            <ListFilter className="w-3.5 h-3.5" />
            <span>Add Category</span>
          </button>

          <button
            onClick={() => openItemModal()}
            className="flex items-center space-x-2 px-4 py-2 bg-slate-900 text-white rounded-full text-xs font-bold hover:bg-slate-800 transition-all shadow-md active:scale-95"
          >
            <Plus className="w-3.5 h-3.5" />
            <span>New Item</span>
          </button>
        </div>
      </div>

      {/* SECOND ROW: SEARCH & ACTION CARDS */}
      <div className="flex flex-col lg:flex-row items-stretch lg:items-center justify-between gap-4">
        {/* Search & Category Filter Bar */}
        <div className="flex-1 flex flex-col sm:flex-row items-center gap-3">
          {/* Search Box */}
          <div className="relative w-full sm:w-80">
            <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search menu items, ingredients..."
              className="w-full bg-rose-50/50 border border-rose-100/80 rounded-full pl-10 pr-4 py-2.5 text-xs text-slate-800 focus:outline-none focus:ring-2 focus:ring-rose-200 placeholder-slate-400 font-medium"
            />
          </div>

          {/* All Categories Dropdown */}
          <select
            value={selectedCategoryId || ''}
            onChange={(e) => setSelectedCategoryId(e.target.value ? Number(e.target.value) : null)}
            className="bg-rose-50/50 border border-rose-100/80 rounded-full px-4 py-2.5 text-xs text-slate-700 font-bold focus:outline-none focus:ring-2 focus:ring-rose-200 cursor-pointer"
          >
            <option value="">All Categories</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>

          {/* Filter Toggle Button */}
          <button className="p-2.5 rounded-full border border-slate-200 bg-white text-slate-600 hover:bg-slate-50 transition-colors">
            <Filter className="w-4 h-4" />
          </button>
        </div>

        {/* Feature Buttons: Happy Hour & Combo Builder */}
        <div className="flex items-center space-x-3">
          <button className="flex items-center space-x-2.5 px-4 py-3 bg-rose-50/60 border border-rose-100 rounded-2xl hover:bg-rose-100/50 transition-all text-left shadow-sm">
            <div className="w-8 h-8 rounded-xl bg-white flex items-center justify-center shadow-xs">
              <Clock className="w-4 h-4 text-rose-500" />
            </div>
            <div>
              <p className="text-[11px] font-extrabold text-slate-800 leading-tight">Happy Hour Pricing</p>
            </div>
          </button>

          <button className="flex items-center space-x-2.5 px-4 py-3 bg-rose-50/60 border border-rose-100 rounded-2xl hover:bg-rose-100/50 transition-all text-left shadow-sm">
            <div className="w-8 h-8 rounded-xl bg-white flex items-center justify-center shadow-xs">
              <Grid className="w-4 h-4 text-rose-500" />
            </div>
            <div>
              <p className="text-[11px] font-extrabold text-slate-800 leading-tight">Combo Builder</p>
            </div>
          </button>
        </div>
      </div>

      {/* THIRD ROW: CATEGORY FILTER TABS */}
      <div className="flex items-center space-x-2 overflow-x-auto pb-2 scrollbar-none">
        <button
          onClick={() => setSelectedCategoryId(null)}
          className={`px-5 py-2 rounded-full text-xs font-bold transition-all whitespace-nowrap ${
            selectedCategoryId === null
              ? 'bg-[#B91C1C] text-white shadow-md shadow-red-900/20'
              : 'bg-rose-50/70 text-slate-700 hover:bg-rose-100'
          }`}
        >
          All Items
        </button>

        {categories.map((cat) => {
          const isSelected = selectedCategoryId === cat.id;
          return (
            <button
              key={cat.id}
              onClick={() => setSelectedCategoryId(cat.id)}
              className={`px-5 py-2 rounded-full text-xs font-bold transition-all whitespace-nowrap ${
                isSelected
                  ? 'bg-[#B91C1C] text-white shadow-md shadow-red-900/20'
                  : 'bg-rose-50/70 text-slate-700 hover:bg-rose-100'
              }`}
            >
              {cat.name}
            </button>
          );
        })}

        <button
          onClick={() => setIsCategoryModalOpen(true)}
          className="flex items-center space-x-1 px-3.5 py-2 text-xs font-extrabold text-slate-800 hover:text-red-700 transition-colors whitespace-nowrap"
        >
          <Plus className="w-3.5 h-3.5" />
          <span>New Category</span>
        </button>
      </div>

      {/* FOURTH ROW: MENU ITEMS CARDS GRID */}
      {loading ? (
        <div className="flex flex-col items-center justify-center py-20 bg-white rounded-3xl border border-slate-100 shadow-soft-card">
          <Loader2 className="w-8 h-8 text-red-600 animate-spin mb-3" />
          <p className="text-xs font-bold text-slate-600">Loading culinary offerings from Spring Boot API...</p>
        </div>
      ) : items.length === 0 ? (
        <div className="bg-white rounded-3xl p-12 text-center border border-slate-100 shadow-soft-card max-w-md mx-auto">
          <p className="text-base font-extrabold text-slate-800 mb-1">No Menu Items Found</p>
          <p className="text-xs text-slate-500 mb-6">No items match your search or category criteria.</p>
          <button
            onClick={() => openItemModal()}
            className="px-5 py-2.5 bg-red-700 text-white text-xs font-bold rounded-full hover:bg-red-800 transition-all shadow-md"
          >
            + Create First Item
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {items.map((item) => {
            const isLowStock = item.status === 'LOW_STOCK' || (item.stockQuantity > 0 && item.stockQuantity <= 10);
            const isOutOfStock = item.stockQuantity === 0 || item.status === 'OUT_OF_STOCK';

            return (
              <div
                key={item.id}
                className="bg-white rounded-3xl p-4 border border-slate-100 shadow-soft-card hover:shadow-xl transition-all duration-300 flex flex-col justify-between group"
              >
                <div>
                  {/* Image Container with Badges */}
                  <div className="relative w-full h-44 rounded-2xl overflow-hidden mb-3 bg-slate-100">
                    <img
                      src={
                        item.imageUrl
                          ? item.imageUrl.startsWith('http')
                            ? item.imageUrl
                            : `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}${item.imageUrl}`
                          : 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=600&q=80'
                      }
                      alt={item.name}
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                    />

                    {/* Top Left Live / Hidden Pill */}
                    <button
                      onClick={() => handleToggleVisibility(item)}
                      className={`absolute top-3 left-3 px-3 py-1 rounded-full text-[10px] font-extrabold flex items-center space-x-1.5 backdrop-blur-md transition-all ${
                        item.isVisible
                          ? 'bg-white/90 text-slate-800 shadow-xs'
                          : 'bg-slate-900/80 text-white shadow-xs'
                      }`}
                    >
                      <span
                        className={`w-1.5 h-1.5 rounded-full ${
                          item.isVisible ? 'bg-emerald-500' : 'bg-slate-400'
                        }`}
                      />
                      <span>{item.isVisible ? 'Live' : 'Hidden'}</span>
                    </button>

                    {/* Top Right Stock Pill */}
                    <button
                      onClick={() => {
                        setStockEditItem(item);
                        setNewStockVal(item.stockQuantity.toString());
                      }}
                      className={`absolute top-3 right-3 px-3 py-1 rounded-full text-[10px] font-extrabold backdrop-blur-md transition-all ${
                        isOutOfStock
                          ? 'bg-slate-900/90 text-white'
                          : isLowStock
                          ? 'bg-rose-100/90 text-rose-800 border border-rose-200'
                          : 'bg-white/90 text-slate-800'
                      }`}
                    >
                      <span>
                        Stock: {item.stockQuantity} {isLowStock ? '(Low)' : ''}
                      </span>
                    </button>
                  </div>

                  {/* Title & Category Subtitle */}
                  <div className="mb-2">
                    <h3 className="text-sm font-extrabold text-slate-900 line-clamp-1 leading-snug">
                      {item.name}
                    </h3>
                    <p className="text-[11px] font-semibold text-slate-400">
                      {item.categoryName || categories.find((c) => c.id === item.categoryId)?.name || 'Category'}
                    </p>
                  </div>

                  {/* Dietary & Allergen Badges */}
                  <div className="flex flex-wrap items-center gap-1.5 mb-4">
                    {item.dietaryTags?.map((tag, idx) => (
                      <span
                        key={idx}
                        className="px-2.5 py-0.5 rounded-md bg-rose-50 text-slate-700 text-[10px] font-bold border border-rose-100"
                      >
                        {tag}
                      </span>
                    ))}

                    {item.calories && (
                      <span className="px-2.5 py-0.5 rounded-md bg-rose-50 text-slate-700 text-[10px] font-bold border border-rose-100 flex items-center space-x-0.5">
                        <Flame className="w-3 h-3 text-amber-500 fill-amber-500" />
                        <span>{item.calories} kcal</span>
                      </span>
                    )}

                    {item.allergens?.map((allergen, idx) => (
                      <span
                        key={idx}
                        className="px-2.5 py-0.5 rounded-md bg-rose-50 text-slate-700 text-[10px] font-bold border border-rose-100"
                      >
                        {allergen}
                      </span>
                    ))}
                  </div>
                </div>

                {/* Card Footer: Price & Edit Icon */}
                <div className="pt-2 border-t border-slate-100 flex items-center justify-between">
                  <span className="text-base font-black text-[#B91C1C]">
                    ${typeof item.price === 'number' ? item.price.toFixed(2) : parseFloat(item.price).toFixed(2)}
                  </span>

                  <div className="flex items-center space-x-1.5">
                    <button
                      onClick={() => openItemModal(item)}
                      className="p-2 rounded-full border border-slate-200 hover:border-slate-400 hover:bg-slate-50 transition-colors text-slate-700"
                      title="Edit Item"
                    >
                      <Pencil className="w-3.5 h-3.5" />
                    </button>

                    <button
                      onClick={() => handleDeleteClick(item)}
                      className="p-2 rounded-full border border-slate-200 hover:border-rose-300 hover:bg-rose-50 transition-colors text-slate-400 hover:text-rose-600 cursor-pointer"
                      title="Delete Item"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* ADD / EDIT ITEM MODAL */}
      <AddNewItemModal
        isOpen={isItemModalOpen}
        onClose={() => {
          setIsItemModalOpen(false);
          setEditingItem(null);
        }}
        categories={categories}
        initialItem={editingItem}
        onSuccess={(item, isEdit) => {
          if (isEdit) {
            setItems((prev) => prev.map((i) => (i.id === item.id ? item : i)));
            setSuccessMsg(`Successfully updated "${item.name}"`);
          } else {
            setItems((prev) => [item, ...prev]);
            setSuccessMsg(`Successfully added "${item.name}" to menu`);
          }
          loadData();
        }}
      />

      {/* CREATE CATEGORY MODAL */}
      {isCategoryModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-sm w-full p-6 shadow-2xl border border-slate-100 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <h3 className="text-base font-extrabold text-slate-900">Add New Category</h3>
              <button
                onClick={() => setIsCategoryModalOpen(false)}
                className="p-1 rounded-lg text-slate-400 hover:text-slate-700"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCategorySubmit} className="space-y-3.5">
              <div>
                <label className="block text-[11px] font-bold text-slate-700 mb-1">Category Name *</label>
                <input
                  type="text"
                  required
                  value={newCatName}
                  onChange={(e) => setNewCatName(e.target.value)}
                  placeholder="e.g. Healthy Bowls"
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3.5 py-2 text-xs text-slate-800 font-medium focus:outline-none focus:ring-2 focus:ring-rose-300"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-700 mb-1">Description</label>
                <input
                  type="text"
                  value={newCatDesc}
                  onChange={(e) => setNewCatDesc(e.target.value)}
                  placeholder="Optional description"
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3.5 py-2 text-xs text-slate-800 font-medium focus:outline-none focus:ring-2 focus:ring-rose-300"
                />
              </div>

              <div className="pt-3 border-t border-slate-100 flex items-center justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => setIsCategoryModalOpen(false)}
                  className="px-4 py-2 text-xs font-bold text-slate-600 hover:bg-slate-100 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="px-5 py-2 bg-[#B91C1C] hover:bg-red-800 text-white text-xs font-bold rounded-xl shadow-md transition-all flex items-center space-x-2"
                >
                  {isSubmitting && <Loader2 className="w-3.5 h-3.5 animate-spin" />}
                  <span>Save Category</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* QUICK STOCK EDIT MODAL */}
      {stockEditItem && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-xs w-full p-6 shadow-2xl border border-slate-100 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <h3 className="text-sm font-extrabold text-slate-900">Update Stock</h3>
              <button
                onClick={() => setStockEditItem(null)}
                className="p-1 rounded-lg text-slate-400 hover:text-slate-700"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleStockUpdate} className="space-y-3.5">
              <div>
                <p className="text-xs font-bold text-slate-800 mb-1">{stockEditItem.name}</p>
                <label className="block text-[11px] font-semibold text-slate-500 mb-1">
                  New Stock Quantity
                </label>
                <input
                  type="number"
                  required
                  min="0"
                  value={newStockVal}
                  onChange={(e) => setNewStockVal(e.target.value)}
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3.5 py-2 text-xs text-slate-800 font-bold focus:outline-none focus:ring-2 focus:ring-rose-300"
                />
              </div>

              <div className="pt-2 flex items-center justify-end space-x-2">
                <button
                  type="button"
                  onClick={() => setStockEditItem(null)}
                  className="px-3 py-1.5 text-xs font-bold text-slate-600 hover:bg-slate-100 rounded-lg"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-1.5 bg-slate-900 text-white text-xs font-bold rounded-lg shadow-sm"
                >
                  Update
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* DELETE CONFIRMATION MODAL */}
      {deleteConfirmItem && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 animate-in fade-in duration-200">
          <div className="bg-white rounded-3xl max-w-sm w-full p-6 shadow-2xl border border-slate-100 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <div className="flex items-center space-x-2 text-rose-600">
                <AlertCircle className="w-5 h-5 text-rose-500" />
                <h3 className="text-base font-extrabold text-slate-900">Delete Item</h3>
              </div>
              <button
                type="button"
                onClick={() => setDeleteConfirmItem(null)}
                className="p-1 rounded-lg text-slate-400 hover:text-slate-700 cursor-pointer"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-2">
              <p className="text-sm font-semibold text-slate-800">
                Are you sure you want to delete this item?
              </p>
              <div className="p-3 bg-slate-50 border border-slate-200 rounded-xl">
                <p className="text-xs font-bold text-slate-900">{deleteConfirmItem.name}</p>
                {deleteConfirmItem.categoryName && (
                  <p className="text-[11px] text-slate-500 mt-0.5">{deleteConfirmItem.categoryName}</p>
                )}
              </div>
              <p className="text-xs text-slate-500">
                This action cannot be undone. The item will be permanently removed from your menu.
              </p>
            </div>

            <div className="pt-3 border-t border-slate-100 flex items-center justify-end space-x-3">
              <button
                type="button"
                disabled={isDeleting}
                onClick={() => setDeleteConfirmItem(null)}
                className="px-4 py-2 text-xs font-bold text-slate-600 hover:bg-slate-100 rounded-xl cursor-pointer transition-colors"
              >
                Cancel
              </button>
              <button
                type="button"
                disabled={isDeleting}
                onClick={handleConfirmDelete}
                className="px-5 py-2 bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold rounded-xl shadow-md transition-all flex items-center space-x-2 cursor-pointer active:scale-95"
              >
                {isDeleting && <Loader2 className="w-3.5 h-3.5 animate-spin" />}
                <span>Delete</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
