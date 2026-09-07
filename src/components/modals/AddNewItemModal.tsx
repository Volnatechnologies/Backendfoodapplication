import React, { useState, useRef, useEffect } from 'react';
import {
  X,
  Camera,
  ChevronDown,
  Loader2,
  AlertCircle,
  Leaf,
  Egg,
  Wheat,
  Drumstick,
  RefreshCw,
  Trash2
} from 'lucide-react';
import type { Category, MenuItem } from '../../services/api';
import { createMenuItem, updateMenuItem, BASE_URL } from '../../services/api';

interface AddNewItemModalProps {
  isOpen: boolean;
  onClose: () => void;
  categories: Category[];
  initialItem?: MenuItem | null;
  onSuccess: (item: MenuItem, isEdit: boolean) => void;
}

export const AddNewItemModal: React.FC<AddNewItemModalProps> = ({
  isOpen,
  onClose,
  categories,
  initialItem,
  onSuccess,
}) => {
  // Form State
  const [name, setName] = useState('');
  const [categoryId, setCategoryId] = useState<number | ''>('');
  const [internalCode, setInternalCode] = useState('');
  const [description, setDescription] = useState('');

  // Photo
  const [imageFile, setImageFile] = useState<File | undefined>(undefined);
  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  // Pricing & Availability
  const [basePrice, setBasePrice] = useState('');
  const [discountedPrice, setDiscountedPrice] = useState('');
  const [isLive, setIsLive] = useState(true);

  // Dietary Attributes
  const [isVeg, setIsVeg] = useState(false);
  const [isNonVeg, setIsNonVeg] = useState(false);
  const [containsEgg, setContainsEgg] = useState(false);
  const [isGlutenFree, setIsGlutenFree] = useState(false);

  // Spice Level
  const [spiceLevel, setSpiceLevel] = useState<'MILD' | 'MEDIUM' | 'HOT'>('MEDIUM');

  // Nutritional Info
  const [calories, setCalories] = useState('');
  const [protein, setProtein] = useState('');
  const [carbohydrates, setCarbohydrates] = useState('');
  const [fats, setFats] = useState('');

  // UI status
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  // Initialize or reset form when modal opens or initialItem changes
  useEffect(() => {
    if (isOpen) {
      setErrorMessage(null);
      if (initialItem) {
        setName(initialItem.name || '');
        setCategoryId(initialItem.categoryId || (categories.length > 0 ? categories[0].id : ''));
        setInternalCode(initialItem.internalCode || '');
        setDescription(initialItem.description || '');
        setBasePrice(initialItem.price !== undefined ? initialItem.price.toString() : '');
        setDiscountedPrice(initialItem.discountedPrice !== undefined && initialItem.discountedPrice !== null ? initialItem.discountedPrice.toString() : '');
        setIsLive(initialItem.isVisible !== undefined ? initialItem.isVisible : true);

        // Dietary
        const tags = initialItem.dietaryTags || [];
        setIsVeg(tags.includes('Veg') || tags.includes('Vegetarian'));
        setIsNonVeg(tags.includes('Non-Veg') || tags.includes('Non-Vegetarian'));
        setIsGlutenFree(tags.includes('Gluten-Free'));

        // Allergens
        const allergensList = initialItem.allergens || [];
        setContainsEgg(allergensList.includes('Contains Egg') || tags.includes('Contains Egg'));

        // Spice Level
        if (initialItem.spicyLevel === 'MILD') setSpiceLevel('MILD');
        else if (initialItem.spicyLevel === 'HOT' || initialItem.spicyLevel === 'EXTRA_HOT') setSpiceLevel('HOT');
        else setSpiceLevel('MEDIUM');

        // Nutrition
        setCalories(initialItem.calories !== undefined && initialItem.calories !== null ? initialItem.calories.toString() : '');
        setProtein(initialItem.protein !== undefined && initialItem.protein !== null ? initialItem.protein.toString() : '');
        setCarbohydrates(initialItem.carbohydrates !== undefined && initialItem.carbohydrates !== null ? initialItem.carbohydrates.toString() : '');
        setFats(initialItem.fats !== undefined && initialItem.fats !== null ? initialItem.fats.toString() : '');

        // Image
        setImageFile(undefined);
        if (initialItem.imageUrl) {
          const imgUrl = initialItem.imageUrl.startsWith('http')
            ? initialItem.imageUrl
            : `${BASE_URL}${initialItem.imageUrl.startsWith('/') ? '' : '/'}${initialItem.imageUrl}`;
          setImagePreview(imgUrl);
        } else {
          setImagePreview(null);
        }
      } else {
        setName('');
        setCategoryId(categories.length > 0 ? categories[0].id : '');
        setInternalCode('');
        setDescription('');
        setImageFile(undefined);
        setImagePreview(null);
        setBasePrice('');
        setDiscountedPrice('');
        setIsLive(true);
        setIsVeg(false);
        setIsNonVeg(false);
        setContainsEgg(false);
        setIsGlutenFree(false);
        setSpiceLevel('MEDIUM');
        setCalories('');
        setProtein('');
        setCarbohydrates('');
        setFats('');
      }
    }
  }, [isOpen, initialItem, categories]);

  if (!isOpen) return null;

  // Image handlers
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      if (file.size > 5 * 1024 * 1024) {
        setErrorMessage('Image size must be under 5MB');
        return;
      }
      setImageFile(file);
      setImagePreview(URL.createObjectURL(file));
      setErrorMessage(null);
    }
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      const file = e.dataTransfer.files[0];
      if (file.size > 5 * 1024 * 1024) {
        setErrorMessage('Image size must be under 5MB');
        return;
      }
      setImageFile(file);
      setImagePreview(URL.createObjectURL(file));
      setErrorMessage(null);
    }
  };

  const handleRemoveImage = (e: React.MouseEvent) => {
    e.stopPropagation();
    setImageFile(undefined);
    if (imagePreview && imagePreview.startsWith('blob:')) {
      URL.revokeObjectURL(imagePreview);
    }
    setImagePreview(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    // Form Validations
    if (!name.trim()) {
      setErrorMessage('Item Name is required');
      return;
    }
    if (!categoryId) {
      setErrorMessage('Please select a Category');
      return;
    }
    if (!basePrice || isNaN(Number(basePrice)) || Number(basePrice) < 0) {
      setErrorMessage('Please enter a valid Base Price (greater than or equal to 0)');
      return;
    }
    if (discountedPrice && (isNaN(Number(discountedPrice)) || Number(discountedPrice) < 0)) {
      setErrorMessage('Discounted Price must be zero or positive');
      return;
    }
    if (discountedPrice && Number(discountedPrice) > Number(basePrice)) {
      setErrorMessage('Discounted Price cannot be greater than Base Price');
      return;
    }
    if (calories && (isNaN(Number(calories)) || Number(calories) < 0)) {
      setErrorMessage('Calories must be zero or positive');
      return;
    }
    if (protein && (isNaN(Number(protein)) || Number(protein) < 0)) {
      setErrorMessage('Protein must be zero or positive');
      return;
    }
    if (carbohydrates && (isNaN(Number(carbohydrates)) || Number(carbohydrates) < 0)) {
      setErrorMessage('Carbohydrates must be zero or positive');
      return;
    }
    if (fats && (isNaN(Number(fats)) || Number(fats) < 0)) {
      setErrorMessage('Fats must be zero or positive');
      return;
    }

    // Build dietary tags & allergens
    const dietaryTags: string[] = [];
    if (isVeg) dietaryTags.push('Veg');
    if (isNonVeg) dietaryTags.push('Non-Veg');
    if (isGlutenFree) dietaryTags.push('Gluten-Free');

    const allergens: string[] = [];
    if (containsEgg) allergens.push('Contains Egg');

    // Build payload
    const payload: any = {
      name: name.trim(),
      description: description.trim() || undefined,
      internalCode: internalCode.trim() || undefined,
      price: parseFloat(basePrice),
      discountedPrice: discountedPrice ? parseFloat(discountedPrice) : undefined,
      categoryId: Number(categoryId),
      calories: calories ? parseInt(calories, 10) : undefined,
      protein: protein ? parseFloat(protein) : undefined,
      carbohydrates: carbohydrates ? parseFloat(carbohydrates) : undefined,
      fats: fats ? parseFloat(fats) : undefined,
      dietaryTags,
      allergens,
      spicyLevel: spiceLevel,
      stockQuantity: initialItem ? initialItem.stockQuantity : 10,
      status: initialItem ? initialItem.status : 'IN_STOCK',
      isVisible: isLive,
      isAvailable: isLive,
      imageUrl: imagePreview
        ? imagePreview.startsWith('blob:')
          ? undefined
          : initialItem?.imageUrl
        : '',
    };

    setIsSubmitting(true);
    try {
      if (initialItem) {
        const updated = await updateMenuItem(initialItem.id, payload, imageFile);
        onSuccess(updated, true);
      } else {
        const created = await createMenuItem(payload, imageFile);
        onSuccess(created, false);
      }
      onClose();
    } catch (err: any) {
      setErrorMessage(err.message || 'Failed to save menu item. Please verify the entered data.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-3 sm:p-4 animate-in fade-in duration-200">
      <div className="bg-white rounded-3xl max-w-2xl w-full shadow-2xl overflow-hidden flex flex-col max-h-[92vh]">
        {/* MODAL HEADER */}
        <div className="bg-[#5DADE2] px-6 py-4 flex items-start justify-between">
          <div>
            <h2 className="text-xl font-bold text-slate-900 leading-tight">
              {initialItem ? 'Edit Item' : 'Add New Item'}
            </h2>
            <p className="text-xs text-slate-700 mt-0.5">
              {initialItem ? 'Update details for this culinary offering.' : 'Expand your culinary offerings.'}
            </p>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="p-1 rounded-lg text-slate-800 hover:text-black hover:bg-sky-400/50 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* MODAL BODY (SCROLLABLE) */}
        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-6 space-y-6">
          {/* Error Banner */}
          {errorMessage && (
            <div className="bg-red-50 border border-red-200 rounded-2xl p-3.5 flex items-start space-x-2.5 text-red-700 text-xs">
              <AlertCircle className="w-4 h-4 text-red-500 shrink-0 mt-0.5" />
              <div className="flex-1 font-semibold">{errorMessage}</div>
              <button
                type="button"
                onClick={() => setErrorMessage(null)}
                className="text-red-400 hover:text-red-700"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
          )}

          {/* SECTION 1: BASIC INFORMATION & ITEM PHOTO */}
          <div className="grid grid-cols-1 md:grid-cols-12 gap-5">
            {/* Left Column: Basic Information */}
            <div className="md:col-span-8 space-y-3.5">
              <h3 className="text-sm font-bold text-slate-900">Basic Information</h3>

              {/* Item Name */}
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Item Name</label>
                <input
                  type="text"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="e.g., Artisan Avocado Toast"
                  className="w-full rounded-2xl border border-slate-300 px-4 py-2 text-xs text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-[#5DADE2] focus:ring-1 focus:ring-[#5DADE2] transition-colors"
                  required
                />
              </div>

              {/* Category & Internal Code */}
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Category</label>
                  <div className="relative">
                    <select
                      value={categoryId}
                      onChange={(e) => setCategoryId(Number(e.target.value))}
                      className="w-full rounded-2xl border border-slate-300 px-4 py-2 text-xs text-slate-900 appearance-none bg-white pr-9 focus:outline-none focus:border-[#5DADE2] focus:ring-1 focus:ring-[#5DADE2] transition-colors cursor-pointer"
                      required
                    >
                      <option value="" disabled>Select a category</option>
                      {categories.map((cat) => (
                        <option key={cat.id} value={cat.id}>
                          {cat.name}
                        </option>
                      ))}
                    </select>
                    <ChevronDown className="w-4 h-4 text-slate-500 absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none" />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Internal Code (Optional)</label>
                  <input
                    type="text"
                    value={internalCode}
                    onChange={(e) => setInternalCode(e.target.value)}
                    placeholder="e.g., BRK-001"
                    className="w-full rounded-2xl border border-slate-300 px-4 py-2 text-xs text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-[#5DADE2] focus:ring-1 focus:ring-[#5DADE2] transition-colors"
                  />
                </div>
              </div>

              {/* Description */}
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Description</label>
                <textarea
                  rows={3}
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Describe the dish..."
                  className="w-full rounded-2xl border border-slate-300 px-4 py-2 text-xs text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-[#5DADE2] focus:ring-1 focus:ring-[#5DADE2] transition-colors resize-none"
                />
              </div>
            </div>

            {/* Right Column: Item Photo */}
            <div className="md:col-span-4 flex flex-col">
              <h3 className="text-sm font-bold text-slate-900 mb-3">Item Photo</h3>

              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                onChange={handleFileChange}
                className="hidden"
              />

              <div
                onClick={() => fileInputRef.current?.click()}
                onDragOver={(e) => e.preventDefault()}
                onDrop={handleDrop}
                className="flex-1 min-h-[170px] rounded-2xl bg-[#EBF5FB] border border-sky-100 flex flex-col items-center justify-center text-center p-3 relative cursor-pointer hover:bg-sky-100/50 transition-all overflow-hidden group"
              >
                {imagePreview ? (
                  <div className="relative w-full h-full min-h-[160px] flex items-center justify-center">
                    <img
                      src={imagePreview}
                      alt="Preview"
                      className="w-full h-full object-cover rounded-xl"
                    />
                    <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity rounded-xl flex items-center justify-center space-x-2">
                      <button
                        type="button"
                        onClick={(e) => {
                          e.stopPropagation();
                          fileInputRef.current?.click();
                        }}
                        className="p-2 bg-white/90 rounded-full text-slate-700 hover:text-black shadow-sm"
                        title="Replace Image"
                      >
                        <RefreshCw className="w-4 h-4" />
                      </button>
                      <button
                        type="button"
                        onClick={handleRemoveImage}
                        className="p-2 bg-red-600 rounded-full text-white hover:bg-red-700 shadow-sm"
                        title="Remove Image"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                ) : (
                  <>
                    <div className="w-10 h-10 rounded-full bg-sky-100 flex items-center justify-center text-[#2980B9] mb-1.5 shadow-xs">
                      <Camera className="w-5 h-5" />
                    </div>
                    <p className="text-xs font-bold text-slate-800 leading-tight">Click to upload</p>
                    <p className="text-[10px] text-slate-400 mt-0.5">or drag and drop</p>
                    <p className="text-[10px] text-slate-400 mt-0.5">PNG, JPG up to 5MB</p>
                  </>
                )}
              </div>
            </div>
          </div>

          {/* SECTION 2: PRICING & AVAILABILITY */}
          <div className="bg-[#5DADE2] rounded-2xl p-4.5 sm:p-5 text-slate-900 shadow-sm">
            <div className="flex items-center justify-between mb-3.5">
              <h3 className="text-sm font-bold text-slate-900">Pricing & Availability</h3>
              <div className="flex items-center space-x-2 text-xs font-semibold text-slate-800">
                <span>Hidden</span>
                <button
                  type="button"
                  onClick={() => setIsLive(!isLive)}
                  className={`w-11 h-6 rounded-full p-1 transition-colors flex items-center ${
                    isLive ? 'bg-[#DC2626]' : 'bg-slate-300'
                  }`}
                >
                  <div
                    className={`bg-white w-4 h-4 rounded-full shadow-sm transform transition-transform ${
                      isLive ? 'translate-x-5' : 'translate-x-0'
                    }`}
                  />
                </button>
                <span>Live</span>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
              <div>
                <label className="block text-xs font-medium text-slate-800 mb-1">Base Price</label>
                <div className="relative">
                  <span className="absolute left-4 top-1/2 -translate-y-1/2 text-xs text-slate-400 font-medium">$</span>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    value={basePrice}
                    onChange={(e) => setBasePrice(e.target.value)}
                    placeholder="0.00"
                    className="w-full bg-white rounded-full pl-7 pr-4 py-2 text-xs text-slate-900 placeholder:text-slate-400 border-0 shadow-xs focus:outline-none focus:ring-2 focus:ring-sky-200"
                    required
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-800 mb-1">Discounted Price (Optional)</label>
                <div className="relative">
                  <span className="absolute left-4 top-1/2 -translate-y-1/2 text-xs text-slate-400 font-medium">$</span>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    value={discountedPrice}
                    onChange={(e) => setDiscountedPrice(e.target.value)}
                    placeholder="0.00"
                    className="w-full bg-white rounded-full pl-7 pr-4 py-2 text-xs text-slate-900 placeholder:text-slate-400 border-0 shadow-xs focus:outline-none focus:ring-2 focus:ring-sky-200"
                  />
                </div>
              </div>
            </div>
          </div>

          {/* SECTION 3: DIETARY ATTRIBUTES, SPICE LEVEL & NUTRITIONAL INFO */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pt-1">
            {/* Left: Dietary Attributes & Spice Level */}
            <div>
              <h3 className="text-sm font-bold text-slate-900 mb-3">Dietary Attributes</h3>

              <div className="space-y-2.5">
                {/* Vegetarian */}
                <label className="flex items-center justify-between cursor-pointer py-0.5">
                  <div className="flex items-center space-x-2.5">
                    <input
                      type="checkbox"
                      checked={isVeg}
                      onChange={(e) => {
                        setIsVeg(e.target.checked);
                        if (e.target.checked) setIsNonVeg(false);
                      }}
                      className="w-4 h-4 rounded border-slate-300 text-emerald-600 focus:ring-emerald-500 cursor-pointer"
                    />
                    <span className="text-xs text-slate-700 font-medium">Vegetarian (Veg)</span>
                  </div>
                  <div className="w-5 h-5 rounded-full bg-emerald-50 flex items-center justify-center text-emerald-600">
                    <Leaf className="w-3.5 h-3.5" />
                  </div>
                </label>

                {/* Non-Vegetarian */}
                <label className="flex items-center justify-between cursor-pointer py-0.5">
                  <div className="flex items-center space-x-2.5">
                    <input
                      type="checkbox"
                      checked={isNonVeg}
                      onChange={(e) => {
                        setIsNonVeg(e.target.checked);
                        if (e.target.checked) setIsVeg(false);
                      }}
                      className="w-4 h-4 rounded border-slate-300 text-red-600 focus:ring-red-500 cursor-pointer"
                    />
                    <span className="text-xs text-slate-700 font-medium">Non-Vegetarian</span>
                  </div>
                  <div className="w-5 h-5 rounded-full bg-red-50 flex items-center justify-center text-red-600">
                    <Drumstick className="w-3.5 h-3.5" />
                  </div>
                </label>

                {/* Contains Egg */}
                <label className="flex items-center justify-between cursor-pointer py-0.5">
                  <div className="flex items-center space-x-2.5">
                    <input
                      type="checkbox"
                      checked={containsEgg}
                      onChange={(e) => setContainsEgg(e.target.checked)}
                      className="w-4 h-4 rounded border-slate-300 text-amber-500 focus:ring-amber-400 cursor-pointer"
                    />
                    <span className="text-xs text-slate-700 font-medium">Contains Egg</span>
                  </div>
                  <div className="w-5 h-5 rounded-full bg-amber-50 flex items-center justify-center text-amber-500">
                    <Egg className="w-3.5 h-3.5" />
                  </div>
                </label>

                {/* Gluten-Free */}
                <label className="flex items-center justify-between cursor-pointer py-0.5">
                  <div className="flex items-center space-x-2.5">
                    <input
                      type="checkbox"
                      checked={isGlutenFree}
                      onChange={(e) => setIsGlutenFree(e.target.checked)}
                      className="w-4 h-4 rounded border-slate-300 text-orange-500 focus:ring-orange-400 cursor-pointer"
                    />
                    <span className="text-xs text-slate-700 font-medium">Gluten-Free</span>
                  </div>
                  <div className="w-5 h-5 rounded-full bg-orange-50 flex items-center justify-center text-orange-500">
                    <Wheat className="w-3.5 h-3.5" />
                  </div>
                </label>
              </div>

              {/* Spice Level */}
              <div className="mt-4 pt-1">
                <label className="block text-xs font-bold text-slate-800 mb-2">Spice Level</label>
                <div className="flex items-center space-x-2">
                  {(['MILD', 'MEDIUM', 'HOT'] as const).map((lvl) => {
                    const isSelected = spiceLevel === lvl;
                    const label = lvl === 'MILD' ? 'Mild' : lvl === 'MEDIUM' ? 'Medium' : 'Hot';
                    return (
                      <button
                        key={lvl}
                        type="button"
                        onClick={() => setSpiceLevel(lvl)}
                        className={`px-5 py-1.5 rounded-full text-xs font-medium transition-all cursor-pointer ${
                          isSelected
                            ? 'bg-black text-white border border-black shadow-xs'
                            : 'bg-white text-slate-700 border border-slate-300 hover:border-slate-400'
                        }`}
                      >
                        {label}
                      </button>
                    );
                  })}
                </div>
              </div>
            </div>

            {/* Right: Nutritional Info */}
            <div className="md:border-l md:border-slate-100 md:pl-6 space-y-3">
              <h3 className="text-sm font-bold text-slate-900 mb-3">Nutritional Info</h3>

              <div className="flex items-center justify-between">
                <span className="text-xs text-slate-700 font-medium">Calories (kcal)</span>
                <input
                  type="number"
                  min="0"
                  value={calories}
                  onChange={(e) => setCalories(e.target.value)}
                  placeholder="0"
                  className="w-14 rounded-md border border-slate-300 px-2 py-1 text-xs text-center text-slate-900 focus:outline-none focus:border-[#5DADE2] focus:ring-1 focus:ring-[#5DADE2]"
                />
              </div>

              <div className="flex items-center justify-between">
                <span className="text-xs text-slate-700 font-medium">Protein (g)</span>
                <input
                  type="number"
                  step="0.1"
                  min="0"
                  value={protein}
                  onChange={(e) => setProtein(e.target.value)}
                  placeholder="0"
                  className="w-14 rounded-md border border-slate-300 px-2 py-1 text-xs text-center text-slate-900 focus:outline-none focus:border-[#5DADE2] focus:ring-1 focus:ring-[#5DADE2]"
                />
              </div>

              <div className="flex items-center justify-between">
                <span className="text-xs text-slate-700 font-medium">Carbohydrates (g)</span>
                <input
                  type="number"
                  step="0.1"
                  min="0"
                  value={carbohydrates}
                  onChange={(e) => setCarbohydrates(e.target.value)}
                  placeholder="0"
                  className="w-14 rounded-md border border-slate-300 px-2 py-1 text-xs text-center text-slate-900 focus:outline-none focus:border-[#5DADE2] focus:ring-1 focus:ring-[#5DADE2]"
                />
              </div>

              <div className="flex items-center justify-between">
                <span className="text-xs text-slate-700 font-medium">Fats (g)</span>
                <input
                  type="number"
                  step="0.1"
                  min="0"
                  value={fats}
                  onChange={(e) => setFats(e.target.value)}
                  placeholder="0"
                  className="w-14 rounded-md border border-slate-300 px-2 py-1 text-xs text-center text-slate-900 focus:outline-none focus:border-[#5DADE2] focus:ring-1 focus:ring-[#5DADE2]"
                />
              </div>
            </div>
          </div>

          {/* Hidden submit trigger */}
          <button type="submit" className="hidden" />
        </form>

        {/* MODAL FOOTER */}
        <div className="bg-[#5DADE2] px-6 py-3.5 flex items-center justify-end space-x-3">
          <button
            type="button"
            onClick={onClose}
            disabled={isSubmitting}
            className="px-6 py-2 rounded-xl text-xs font-semibold bg-[#94A3B8] text-slate-900 hover:bg-slate-400 transition-colors cursor-pointer"
          >
            Cancel
          </button>
          <button
            type="button"
            disabled={isSubmitting}
            onClick={handleSubmit}
            className="px-6 py-2 rounded-xl text-xs font-bold bg-black text-white hover:bg-slate-900 transition-all shadow-md active:scale-95 flex items-center space-x-2 cursor-pointer"
          >
            {isSubmitting && <Loader2 className="w-3.5 h-3.5 animate-spin" />}
            <span>{initialItem ? 'Save Changes' : 'Add to Menu'}</span>
          </button>
        </div>
      </div>
    </div>
  );
};
