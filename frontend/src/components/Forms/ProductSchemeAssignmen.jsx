import { useForm } from 'react-hook-form'
import { X } from 'lucide-react'
import { useState, useEffect } from 'react'
import api from "../../constants/API/axiosInstance"
import { toast } from 'react-toastify'

// ==================== FORM COMPONENTS ====================

const Input = ({ label, name, register, errors, required = false, type = "text", ...props }) => (
    <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
            {label} {required && <span className="text-red-500">*</span>}
        </label>
        <input
            {...register(name, {
                required: required ? `${label} is required` : false,
            })}
            type={type}
            className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors[name] ? 'border-red-500' : 'border-gray-300'
                }`}
            {...props}
        />
        {errors[name] && (
            <p className="mt-1 text-sm text-red-500">{errors[name].message}</p>
        )}
    </div>
)

const Select = ({ label, name, register, errors, options, required = false, ...props }) => (
    <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
            {label} {required && <span className="text-red-500">*</span>}
        </label>
        <select
            {...register(name, { required: required ? `${label} is required` : false })}
            className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors[name] ? 'border-red-500' : 'border-gray-300'
                }`}
            {...props}
        >
            <option value="">Select {label.toLowerCase()}</option>
            {options.map((option) => (
                <option key={option.value} value={option.value}>
                    {option.label}
                </option>
            ))}
        </select>
        {errors[name] && (
            <p className="mt-1 text-sm text-red-500">{errors[name].message}</p>
        )}
    </div>
)

// ==================== UPDATED PRODUCT ASSIGNMENT FORM MODAL ====================
const ProductAssignmentFormModal = ({ onCancel, onSubmit, initialData = null, isEdit = false }) => {
    const [franchises, setFranchises] = useState([])
    const [merchants, setMerchants] = useState([])
    const [franchiseProducts, setFranchiseProducts] = useState([])
    const [merchantProducts, setMerchantProducts] = useState([])
    const [pricingSchemes, setPricingSchemes] = useState([])
    const [globalWarning, setGlobalWarning] = useState(null)  // NEW: Store global warning
    const [selectedSchemeWarning, setSelectedSchemeWarning] = useState(null)  // NEW: Store selected scheme warning
    const [loading, setLoading] = useState(false)
    const [dataInitialized, setDataInitialized] = useState(false)
    // Add loading states for each data fetch
    const [loadingCustomers, setLoadingCustomers] = useState(false)
    const [loadingProducts, setLoadingProducts] = useState(false)
    const [loadingSchemes, setLoadingSchemes] = useState(false)


    const getDefaultValues = () => ({
        customerType: '',
        customerId: '',
        productId: '',
        schemeId: '',
        effectiveDate: '',
        expiryDate: '',
        remarks: ''
    })

    const {
        register,
        handleSubmit,
        formState: { errors },
        watch,
        reset,
        setValue
    } = useForm({
        defaultValues: initialData ? initialData: getDefaultValues()
    })

    const watchedFields = watch(['customerType', 'customerId', 'productId', 'schemeId'])

    // Initialize form data on mount
    useEffect(() => {
        if (isEdit && initialData && !dataInitialized) {
            reset({
                customerType: initialData.customerType?.toUpperCase() || '',
                customerId: initialData.customerId?.toString() || '',
                productId: initialData.productId?.toString() || '',
                schemeId: initialData.schemeId?.toString() || '',
                effectiveDate: initialData.effectiveDate || '',
                expiryDate: initialData.expiryDate || '',
                remarks: initialData.remarks || ''
            })
            setDataInitialized(true)
        } else if (!isEdit && !dataInitialized) {
            reset(getDefaultValues())
            setDataInitialized(true)
        }
    }, [isEdit, initialData, dataInitialized, reset])

    // Fetch franchises or merchants when customer type is selected
    useEffect(() => {
        const fetchCustomerOptions = async () => {
            if (!watchedFields[0]) return

            try {
                setLoadingCustomers(true)
                if (watchedFields[0] === 'FRANCHISE') {
                    const response = await api.get('/franchise')
                    setFranchises(response.data)
                    setMerchants([])
                } else if (watchedFields[0] === 'MERCHANT') {
                    const response = await api.get('/merchants/direct-merchant')
                    setMerchants(response.data)
                    setFranchises([])
                }
            } catch (error) {
                console.error(`Error fetching ${watchedFields[0].toLowerCase()}s:`, error)
                if (watchedFields[0] === 'FRANCHISE') {
                    setFranchises([])
                } else {
                    setMerchants([])
                }
            } finally {
                setLoadingCustomers(false)
            }
        }

        if (dataInitialized) {
            fetchCustomerOptions()
        }
    }, [watchedFields[0], dataInitialized])

    // Fetch franchise products when franchise is selected
    useEffect(() => {
        const fetchFranchiseProducts = async () => {
            if (watchedFields[0] === 'FRANCHISE' && watchedFields[1]) {
                try {
                    setLoadingProducts(true)
                    const response = await api.get(`/franchise/products/${watchedFields[1]}`)
                    setFranchiseProducts(response.data)
                } catch (error) {
                    console.error('Error fetching franchise products:', error)
                    setFranchiseProducts([])
                } finally {
                    setLoadingProducts(false)
                }
            } else if (watchedFields[0] === 'FRANCHISE' && !watchedFields[1]) {
                setFranchiseProducts([])
            }
        }

        if (dataInitialized) {
            fetchFranchiseProducts()
        }
    }, [watchedFields[0], watchedFields[1], dataInitialized])

    // Fetch merchant products when merchant is selected
    useEffect(() => {
        const fetchMerchantProducts = async () => {
            if (watchedFields[0] === 'MERCHANT' && watchedFields[1]) {
                try {
                    setLoadingProducts(true)
                    const response = await api.get(`/merchants/products/${watchedFields[1]}`)
                    setMerchantProducts(response.data)
                } catch (error) {
                    console.error('Error fetching merchant products:', error)
                    setMerchantProducts([])
                } finally {
                    setLoadingProducts(false)
                }
            } else if (watchedFields[0] === 'MERCHANT' && !watchedFields[1]) {
                setMerchantProducts([])
            }
        }

        if (dataInitialized) {
            fetchMerchantProducts()
        }
    }, [watchedFields[0], watchedFields[1], dataInitialized])

    // Fetch pricing schemes when product is selected
    useEffect(() => {
        const fetchPricingSchemes = async () => {
            if (watchedFields[2] && watchedFields[0]) {
                try {
                    setLoadingSchemes(true)
                    const selectedProduct = watchedFields[0] === 'FRANCHISE'
                        ? franchiseProducts.find(p => p.productId.toString() === watchedFields[2])
                        : merchantProducts.find(p => p.productId.toString() === watchedFields[2])

                    if (selectedProduct) {
                        const customerType = watchedFields[0] === 'FRANCHISE' ? 'franchise' : 'direct_merchant'
                        const response = await api.get(
                            `/pricing-schemes/valid-pricing-scheme?productId=${selectedProduct.productId}&productCategory=${selectedProduct.productCategory}&customerType=${customerType}`
                        )

                        // NEW: Handle the new response structure
                        const { schemes, globalWarning: warning } = response.data
                        setPricingSchemes(schemes || [])
                        setGlobalWarning(warning)

                        // Clear selected scheme warning when schemes list changes
                        setSelectedSchemeWarning(null)
                    }
                } catch (error) {
                    console.error('Error fetching pricing schemes:', error)
                    toast.error(error?.response?.data?.message || 'Failed to fetch pricing schemes. Please try again.')
                    setPricingSchemes([])
                    setGlobalWarning(null)
                } finally {
                    setLoadingSchemes(false)
                }
            } else if (!watchedFields[2]) {
                setPricingSchemes([])
                setGlobalWarning(null)
                setSelectedSchemeWarning(null)
            }
        }

        if (dataInitialized) {
            fetchPricingSchemes()
        }
    }, [watchedFields[2], watchedFields[0], franchiseProducts, merchantProducts, dataInitialized])

    // NEW: Update selected scheme warning when scheme selection changes
    useEffect(() => {
        if (watchedFields[3] && pricingSchemes.length > 0) {
            const selectedScheme = pricingSchemes.find(s => s.schemeCode === watchedFields[3])
            setSelectedSchemeWarning(selectedScheme?.warning || null)
        } else {
            setSelectedSchemeWarning(null)
        }
    }, [watchedFields[3], pricingSchemes])

    // Clear dependent fields when customer type changes
    useEffect(() => {
        if (dataInitialized && watchedFields[0] && !isEdit) {
            setValue('customerId', '')
            setValue('productId', '')
            setValue('schemeId', '')
            setFranchiseProducts([])
            setMerchantProducts([])
            setPricingSchemes([])
            setGlobalWarning(null)
            setSelectedSchemeWarning(null)
        }
    }, [watchedFields[0], setValue, dataInitialized, isEdit])

    // Clear product and scheme when customerId changes
    useEffect(() => {
        if (dataInitialized && watchedFields[1] && !isEdit) {
            setValue('productId', '')
            setValue('schemeId', '')
            setPricingSchemes([])
            setGlobalWarning(null)
            setSelectedSchemeWarning(null)
        }
    }, [watchedFields[1], setValue, dataInitialized, isEdit])

    // Clear scheme when product changes
    useEffect(() => {
        if (dataInitialized && watchedFields[2] && !isEdit) {
            setValue('schemeId', '')
            setSelectedSchemeWarning(null)
        }
    }, [watchedFields[2], setValue, dataInitialized, isEdit])

    const customerTypeOptions = [
        { value: 'FRANCHISE', label: 'Franchise' },
        { value: 'MERCHANT', label: 'Merchant' }
    ]

    
    const getCustomerOptions = () => {
        if (watchedFields[0] === 'FRANCHISE') {
            return franchises.map(franchise => ({
                value: franchise.id.toString(),
                label: `${franchise.franchiseName} (ID: ${franchise.id})`
            }))
        } else if (watchedFields[0] === 'MERCHANT') {
            return merchants.map(merchant => ({
                value: merchant.id.toString(),
                label: `${merchant.businessName} (ID: ${merchant.id})`
            }))
        }
        return []
    }

    const getProductOptions = () => {
        const products = watchedFields[0] === 'FRANCHISE' ? franchiseProducts : merchantProducts
        return products.map(product => ({
            value: product.productId.toString(),
            label: `${product.productName} (${product.productCode}) - Qty: ${product.totalQuantity}`
        }))
    }

    const getSchemeOptions = () => {
        return pricingSchemes.map(scheme => ({
            value: scheme.schemeId,
            label: `${scheme.schemeCode} - ₹${scheme.monthlyRent}/month`
        }))
    }

    const onFormSubmit = async (data) => {
        try {
            setLoading(true)

            const assignmentData = {
                customerType: data.customerType,
                customerId: parseInt(data.customerId),
                productId: parseInt(data.productId),
                schemeId: parseInt(data.schemeId),
                effectiveDate: data.effectiveDate,
                expiryDate: data.expiryDate || null,
                remarks: data.remarks || null
            }

            let response
            if (isEdit && initialData?.id) {
                response = await api.put(`/outward-schemes/${initialData.id}`, assignmentData)
                toast.success("Scheme Assign Successfully Updated")
            } else {
                response = await api.post('/outward-schemes', assignmentData)
                toast.success("Scheme Assigned Successfully")
            }
            
            onSubmit(response.data)
            onCancel()
        } catch (error) {
            console.error('Error saving assignment:', error)
            toast.error(error?.response?.data?.message || 'Failed to save assignment. Please try again.')
        } finally {
            setLoading(false)
        }
    }

    const handleCancel = () => {
        reset(getDefaultValues())
        setDataInitialized(false)
        setGlobalWarning(null)
        setSelectedSchemeWarning(null)
        onCancel()
    }

    // Calculate if we're ready to show the form
    const isLoadingInitialData = isEdit && (
        loadingCustomers ||
        loadingProducts ||
        loadingSchemes ||
        (watchedFields[0] && getCustomerOptions().length === 0) ||
        (watchedFields[1] && getProductOptions().length === 0) ||
        (watchedFields[2] && getSchemeOptions().length === 0)
    )

    // Show loading state
    if (isLoadingInitialData) {
        return (
            <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50">
                <div className="bg-white rounded-lg p-8 text-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
                    <p className="text-gray-600">Loading assignment data...</p>
                </div>
            </div>
        )
    }

    return (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
                {/* Modal Header */}
                <div className="flex justify-between items-center p-6 border-b bg-gray-50 rounded-t-lg">
                    <h2 className="text-2xl font-bold text-gray-800">
                        {isEdit ? 'Edit Customer Scheme Assignment' : 'Add New Customer Scheme Assignment'}
                    </h2>
                    <button
                        onClick={handleCancel}
                        className="p-2 hover:bg-gray-200 rounded-md transition-colors"
                        title="Close"
                    >
                        <X size={24} className="text-gray-600" />
                    </button>
                </div>

                {/* Modal Body */}
                <div className="p-6">
                    <div className="space-y-6">
                        {/* Assignment Details */}
                        <div className="bg-gray-50 p-4 rounded-lg">
                            <h3 className="text-lg font-semibold text-gray-700 mb-4">Assignment Details</h3>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <Select
                                    label="Customer Type"
                                    name="customerType"
                                    register={register}
                                    errors={errors}
                                    options={customerTypeOptions}
                                    required
                                />
                                <Select
                                    label="Customer"
                                    name="customerId"
                                    register={register}
                                    errors={errors}
                                    options={getCustomerOptions()}
                                    required
                                    disabled={!watchedFields[0] || loading}
                                />
                                <Select
                                    label="Product"
                                    name="productId"
                                    register={register}
                                    errors={errors}
                                    options={getProductOptions()}
                                    required
                                    disabled={!watchedFields[1] || loading}
                                />
                                <Select
                                    label="Pricing Scheme"
                                    name="schemeId"
                                    register={register}
                                    errors={errors}
                                    options={getSchemeOptions()}
                                    required
                                    disabled={!watchedFields[2] || loading}
                                />
                                <Input
                                    label="Effective Date"
                                    name="effectiveDate"
                                    register={register}
                                    errors={errors}
                                    type="date"
                                    required
                                />
                                <Input
                                    label="Expiry Date"
                                    name="expiryDate"
                                    register={register}
                                    errors={errors}
                                    type="date"
                                />
                            </div>
                        </div>

                        {/* Remarks */}
                        <div className="bg-gray-50 p-4 rounded-lg">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Remarks
                                </label>
                                <textarea
                                    {...register('remarks')}
                                    rows="3"
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="Enter any additional remarks about the assignment"
                                />
                            </div>
                        </div>

                        {/* NEW: Global Warning Banner - Shows if no vendor rates exist */}
                        {globalWarning && (
                            <div className="bg-yellow-50 border-l-4 border-yellow-400 p-4 rounded-md">
                                <div className="flex">
                                    <div className="flex-shrink-0">
                                        <svg className="h-5 w-5 text-yellow-400" viewBox="0 0 20 20" fill="currentColor">
                                            <path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                                        </svg>
                                    </div>
                                    <div className="ml-3">
                                        <p className="text-sm text-yellow-700 font-medium">
                                            Vendor Rate Warning
                                        </p>
                                        <p className="text-sm text-yellow-700 mt-1">
                                            {globalWarning}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        )}

                        {/* NEW: Selected Scheme Warning - Shows if selected scheme rates are below vendor rates */}
                        {selectedSchemeWarning && (
                            <div className="bg-orange-50 border-l-4 border-orange-400 p-4 rounded-md">
                                <div className="flex">
                                    <div className="flex-shrink-0">
                                        <svg className="h-5 w-5 text-orange-400" viewBox="0 0 20 20" fill="currentColor">
                                            <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                                        </svg>
                                    </div>
                                    <div className="ml-3">
                                        <p className="text-sm text-orange-700 font-medium">
                                            Pricing Scheme Warning
                                        </p>
                                        <p className="text-sm text-orange-700 mt-1">
                                            {selectedSchemeWarning}
                                        </p>
                                        <p className="text-xs text-orange-600 mt-2 italic">
                                            This scheme can still be assigned, but the rates are below vendor costs.
                                        </p>
                                    </div>
                                </div>
                            </div>
                        )}

                        {/* Action Buttons */}
                        <div className="flex justify-end space-x-4 pt-4 border-t">
                            <button
                                type="button"
                                onClick={handleCancel}
                                className="px-6 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors"
                                disabled={loading}
                            >
                                Cancel
                            </button>
                            <button
                                type="button"
                                onClick={handleSubmit(onFormSubmit)}
                                className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors disabled:opacity-50"
                                disabled={loading}
                            >
                                {loading ? 'Processing...' : (isEdit ? 'Update Assignment' : 'Save Assignment')}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default ProductAssignmentFormModal;