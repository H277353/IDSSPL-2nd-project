import React, { useState, useEffect, useMemo } from 'react';
import {
    useReactTable,
    getCoreRowModel,
    getFilteredRowModel,
    getSortedRowModel,
    getPaginationRowModel,
    flexRender,
} from '@tanstack/react-table';
import { 
    ChevronUp, 
    ChevronDown, 
    Search, 
    Calendar,
    Package,
    TrendingUp,
    FileBarChart,
    Truck,
    Users,
    X // Add X icon for clear button
} from 'lucide-react';
import UniversalExportButtons from './UniversalExportButtons';
import { format } from 'date-fns';
import { getAllOutwardTransactions } from '../../constants/API/OutwardTransAPI';

const OutwardTransactionReport = () => {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [globalFilter, setGlobalFilter] = useState('');
    const [dateRange, setDateRange] = useState({
        startDate: '',
        endDate: ''
    });
    const [filters, setFilters] = useState({
        franchise: '',
        merchant: '',
        product: '',
        deliveryMethod: ''
    });

    // Fetch outward transactions data using the API function
    const fetchOutwardTransactions = async () => {
        setLoading(true);
        setError(null);
        try {
            const transactions = await getAllOutwardTransactions();
            
            // Transform data to include serial numbers in flat structure
            const transformedData = [];
            
            transactions.forEach(transaction => {
                if (transaction.serialNumbers && transaction.serialNumbers.length > 0) {
                    // Create a row for each serial number
                    transaction.serialNumbers.forEach(serial => {
                        transformedData.push({
                            id: `${transaction.id}-${serial.id}`,
                            transactionId: transaction.id,
                            deliveryNumber: transaction.deliveryNumber,
                            franchiseId: transaction.franchiseId,
                            franchiseName: transaction.franchiseName,
                            merchantId: transaction.merchantId,
                            merchantName: transaction.merchantName,
                            productCode: transaction.productCode,
                            productName: transaction.productName,
                            dispatchDate: transaction.dispatchDate,
                            dispatchedBy: transaction.dispatchedBy,
                            quantity: transaction.quantity, // Make sure quantity is included here
                            deliveryAddress: transaction.deliveryAddress,
                            contactPerson: transaction.contactPerson,
                            contactPersonNumber: transaction.contactPersonNumber,
                            deliveryMethod: transaction.deliveryMethod,
                            trackingNumber: transaction.trackingNumber,
                            expectedDelivery: transaction.expectedDelivery,
                            remarks: transaction.remarks,
                            // Serial number details
                            serialId: serial.id,
                            sid: serial.sid,
                            mid: serial.mid,
                            tid: serial.tid,
                            vpaid: serial.vpaid,
                        });
                    });
                } else {
                    // If no serial numbers, show transaction without serial details
                    transformedData.push({
                        id: transaction.id,
                        transactionId: transaction.id,
                        deliveryNumber: transaction.deliveryNumber,
                        franchiseId: transaction.franchiseId,
                        franchiseName: transaction.franchiseName,
                        merchantId: transaction.merchantId,
                        merchantName: transaction.merchantName,
                        productCode: transaction.productCode,
                        productName: transaction.productName,
                        dispatchDate: transaction.dispatchDate,
                        dispatchedBy: transaction.dispatchedBy,
                        quantity: transaction.quantity, // Make sure quantity is included here too
                        deliveryAddress: transaction.deliveryAddress,
                        contactPerson: transaction.contactPerson,
                        contactPersonNumber: transaction.contactPersonNumber,
                        deliveryMethod: transaction.deliveryMethod,
                        trackingNumber: transaction.trackingNumber,
                        expectedDelivery: transaction.expectedDelivery,
                        remarks: transaction.remarks,
                        // Empty serial details
                        serialId: null,
                        sid: '-',
                        mid: '-',
                        tid: '-',
                        vpaid: '-',
                    });
                }
            });
            
            setData(transformedData);
        } catch (error) {
            console.error('Error fetching outward transactions:', error);
            setError('Failed to fetch outward transactions. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchOutwardTransactions();
    }, []);

    // Filter data based on date range
    const filteredData = useMemo(() => {
        let filtered = data;
        
        if (dateRange.startDate) {
            filtered = filtered.filter(item => 
                !item.dispatchDate || new Date(item.dispatchDate) >= new Date(dateRange.startDate)
            );
        }
        
        if (dateRange.endDate) {
            filtered = filtered.filter(item => 
                !item.dispatchDate || new Date(item.dispatchDate) <= new Date(dateRange.endDate)
            );
        }
        
        return filtered;
    }, [data, dateRange]);

    // Clear all filters function
    const clearFilters = () => {
        setGlobalFilter('');
        setDateRange({ startDate: '', endDate: '' });
        setFilters({
            franchise: '',
            merchant: '',
            product: '',
            deliveryMethod: ''
        });
    };

    // Table columns definition (same as before)
    const columns = useMemo(() => [
        {
            header: 'Transaction ID',
            accessorKey: 'transactionId',
            cell: ({ getValue }) => (
                <span className="font-mono text-sm">{getValue()}</span>
            )
        },
        {
            header: 'Delivery Number',
            accessorKey: 'deliveryNumber',
            cell: ({ getValue }) => (
                <span className="font-semibold text-blue-600">{getValue()}</span>
            )
        },
        {
            header: 'Customer Type',
            accessorKey: 'customerType',
            cell: ({ row }) => {
                const franchiseName = row.original.franchiseName;
                const merchantName = row.original.merchantName;
                if (franchiseName) {
                    return <span className="px-2 py-1 bg-purple-100 text-purple-800 rounded-full text-xs font-medium">Franchise</span>;
                } else if (merchantName) {
                    return <span className="px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs font-medium">Merchant</span>;
                }
                return <span className="px-2 py-1 bg-gray-100 text-gray-800 rounded-full text-xs font-medium">Unknown</span>;
            }
        },
        {
            header: 'Customer Name',
            accessorKey: 'customerName',
            cell: ({ row }) => {
                const franchiseName = row.original.franchiseName;
                const merchantName = row.original.merchantName;
                return franchiseName || merchantName || '-';
            }
        },
        {
            header: 'Product Code',
            accessorKey: 'productCode',
            cell: ({ getValue }) => (
                <span className="font-mono bg-gray-100 px-2 py-1 rounded text-xs">
                    {getValue()}
                </span>
            )
        },
        {
            header: 'Product Name',
            accessorKey: 'productName',
        },
        {
            header: 'Dispatch Date',
            accessorKey: 'dispatchDate',
            cell: ({ getValue }) => {
                const date = getValue();
                return date ? format(new Date(date), 'dd MMM yyyy') : '-';
            }
        },
        {
            header: 'Dispatched By',
            accessorKey: 'dispatchedBy',
        },
        {
            header: 'Contact Person',
            accessorKey: 'contactPerson',
        },
        {
            header: 'Contact Number',
            accessorKey: 'contactPersonNumber',
            cell: ({ getValue }) => (
                <span className="font-mono text-sm">{getValue()}</span>
            )
        },
        {
            header: 'Delivery Method',
            accessorKey: 'deliveryMethod',
            cell: ({ getValue }) => {
                const method = getValue();
                const colorMap = {
                    'Courier': 'bg-blue-100 text-blue-800',
                    'Self Pickup': 'bg-green-100 text-green-800',
                    'Direct Delivery': 'bg-purple-100 text-purple-800'
                };
                return (
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${colorMap[method] || 'bg-gray-100 text-gray-800'}`}>
                        {method || '-'}
                    </span>
                );
            }
        },
        {
            header: 'Tracking Number',
            accessorKey: 'trackingNumber',
            cell: ({ getValue }) => {
                const tracking = getValue();
                return tracking ? (
                    <span className="font-mono bg-yellow-50 px-2 py-1 rounded text-xs">
                        {tracking}
                    </span>
                ) : '-';
            }
        },
        {
            header: 'Expected Delivery',
            accessorKey: 'expectedDelivery',
            cell: ({ getValue }) => {
                const date = getValue();
                return date ? format(new Date(date), 'dd MMM yyyy') : '-';
            }
        },
        {
            header: 'SID',
            accessorKey: 'sid',
            cell: ({ getValue }) => (
                <span className="font-mono text-xs bg-indigo-50 px-2 py-1 rounded">
                    {getValue()}
                </span>
            )
        },
        {
            header: 'MID',
            accessorKey: 'mid',
            cell: ({ getValue }) => (
                <span className="font-mono text-xs bg-purple-50 px-2 py-1 rounded">
                    {getValue()}
                </span>
            )
        },
        {
            header: 'TID',
            accessorKey: 'tid',
            cell: ({ getValue }) => (
                <span className="font-mono text-xs bg-emerald-50 px-2 py-1 rounded">
                    {getValue()}
                </span>
            )
        },
        {
            header: 'VPAID',
            accessorKey: 'vpaid',
            cell: ({ getValue }) => (
                <span className="font-mono text-xs bg-orange-50 px-2 py-1 rounded">
                    {getValue()}
                </span>
            )
        },
        {
            header: 'Delivery Address',
            accessorKey: 'deliveryAddress',
            cell: ({ getValue }) => {
                const address = getValue();
                return address ? (
                    <span className="text-xs text-gray-600 max-w-xs truncate" title={address}>
                        {address}
                    </span>
                ) : '-';
            }
        },
        {
            header: 'Remarks',
            accessorKey: 'remarks',
            cell: ({ getValue }) => {
                const remark = getValue();
                return remark ? (
                    <span className="text-xs text-gray-600 max-w-xs truncate" title={remark}>
                        {remark}
                    </span>
                ) : '-';
            }
        }
    ], []);

    // Calculate summary data - CORRECTED VERSION
    const summary = useMemo(() => {
        // Get unique transactions first to avoid double counting
        const uniqueTransactions = [...new Set(filteredData.map(item => item.transactionId))];
        const uniqueTransactionData = uniqueTransactions.map(transactionId => 
            filteredData.find(item => item.transactionId === transactionId)
        );
        
        const totalSerials = filteredData.filter(item => item.serialId).length;
        const uniqueFranchises = [...new Set(filteredData.filter(item => item.franchiseName).map(item => item.franchiseName))];
        const uniqueMerchants = [...new Set(filteredData.filter(item => item.merchantName).map(item => item.merchantName))];
        const uniqueProducts = [...new Set(filteredData.map(item => item.productCode))];
        
        // Calculate customer type breakdown based on unique transactions
        const customerTypeBreakdown = uniqueTransactionData.reduce((acc, item) => {
            if (item.franchiseName) {
                acc['Franchise'] = (acc['Franchise'] || 0) + 1;
            } else if (item.merchantName) {
                acc['Merchant'] = (acc['Merchant'] || 0) + 1;
            } else {
                acc['Unknown'] = (acc['Unknown'] || 0) + 1;
            }
            return acc;
        }, {});

        // Calculate delivery method breakdown based on unique transactions
        const deliveryMethodBreakdown = uniqueTransactionData.reduce((acc, item) => {
            const method = item.deliveryMethod || 'Unknown';
            acc[method] = (acc[method] || 0) + 1;
            return acc;
        }, {});

        // Serial status breakdown should still use all rows since each serial has its own status
        const serialStatusBreakdown = filteredData.reduce((acc, item) => {
            const status = item.serialStatus || 'Unknown';
            acc[status] = (acc[status] || 0) + 1;
            return acc;
        }, {});

        // Calculate total quantity from unique transactions only
        const totalQuantity = uniqueTransactionData.reduce((sum, item) => sum + (item.quantity || 0), 0);

        return {
            totalTransactions: uniqueTransactions.length,
            totalSerialNumbers: totalSerials,
            totalFranchises: uniqueFranchises.length,
            totalMerchants: uniqueMerchants.length,
            totalProducts: uniqueProducts.length,
            totalQuantity: totalQuantity, // Now correctly calculated per transaction
            customerTypeBreakdown,
            deliveryMethodBreakdown,
            serialStatusBreakdown,
            averageQuantityPerTransaction: uniqueTransactions.length > 0 ? 
                (totalQuantity / uniqueTransactions.length).toFixed(2) : 0
        };
    }, [filteredData]); // Changed from data to filteredData

    // Initialize table with filtered data
    const table = useReactTable({
        data: filteredData, // Use filtered data instead of raw data
        columns,
        getCoreRowModel: getCoreRowModel(),
        getFilteredRowModel: getFilteredRowModel(),
        getSortedRowModel: getSortedRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
        state: {
            globalFilter,
        },
        onGlobalFilterChange: setGlobalFilter,
        globalFilterFn: 'includesString',
    });

    // Export configurations (same as before)
    const exportColumns = {
        headers: [
            'Transaction ID', 'Delivery Number', 'Customer Type', 'Customer Name', 'Product Code', 'Product Name',
            'Dispatch Date', 'Dispatched By', 'Contact Person', 'Contact Number', 'Delivery Method',
            'Tracking Number', 'Expected Delivery', 'SID', 'MID', 'TID', 'VPAID', 
            'Delivery Address', 'Remarks'
        ],
        keys: [
            'transactionId', 'deliveryNumber', 'customerType', 'customerName', 'productCode', 'productName',
            'dispatchDate', 'dispatchedBy', 'contactPerson', 'contactPersonNumber', 'deliveryMethod',
            'trackingNumber', 'expectedDelivery', 'sid', 'mid', 'tid', 'vpaid', 
            'deliveryAddress', 'remarks'
        ],
        widths: [12, 15, 12, 20, 15, 25, 12, 15, 15, 12, 12, 15, 12, 15, 15, 15, 15, 30, 25]
    };

    const excelTransform = (data) => {
        return data.map(item => ({
            'Transaction ID': item.transactionId,
            'Delivery Number': item.deliveryNumber,
            'Customer Type': item.franchiseName ? 'Franchise' : item.merchantName ? 'Merchant' : 'Unknown',
            'Customer Name': item.franchiseName || item.merchantName || '',
            'Product Code': item.productCode,
            'Product Name': item.productName,
            'Dispatch Date': item.dispatchDate ? format(new Date(item.dispatchDate), 'dd MMM yyyy') : '',
            'Dispatched By': item.dispatchedBy,
            'Contact Person': item.contactPerson,
            'Contact Number': item.contactPersonNumber,
            'Delivery Method': item.deliveryMethod || '',
            'Tracking Number': item.trackingNumber || '',
            'Expected Delivery': item.expectedDelivery ? format(new Date(item.expectedDelivery), 'dd MMM yyyy') : '',
            'SID': item.sid,
            'MID': item.mid,
            'TID': item.tid,
            'VPAID': item.vpaid,
            'Delivery Address': item.deliveryAddress || '',
            'Remarks': item.remarks || ''
        }));
    };

    const summaryConfig = [
        { key: 'totalTransactions', label: 'Total Transactions', formatter: (val) => val.toLocaleString() },
        { key: 'totalQuantity', label: 'Total Quantity', formatter: (val) => val.toLocaleString() }, // Changed from totalSerialNumbers to totalQuantity
        { key: 'totalFranchises', label: 'Total Franchises', formatter: (val) => val.toLocaleString() },
        { key: 'totalMerchants', label: 'Total Merchants', formatter: (val) => val.toLocaleString() },
        { key: 'totalProducts', label: 'Total Products', formatter: (val) => val.toLocaleString() },
        { key: 'averageQuantityPerTransaction', label: 'Avg Qty/Transaction', formatter: (val) => val }
    ];

    if (loading) {
        return (
            <div className="flex items-center justify-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
                <span className="ml-3 text-gray-600">Loading outward transactions...</span>
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex items-center justify-center h-64">
                <div className="text-center">
                    <div className="text-red-600 mb-2">⚠️ Error</div>
                    <div className="text-gray-600 mb-4">{error}</div>
                    <button
                        onClick={fetchOutwardTransactions}
                        className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors"
                    >
                        Retry
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="space-y-6 p-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-3">
                        <Truck className="w-8 h-8 text-indigo-600" />
                        Outward Transaction Report
                    </h1>
                    <p className="text-gray-600 mt-1">
                        Comprehensive report of all outward transactions with delivery and serial number details
                    </p>
                </div>
                <UniversalExportButtons
                    data={filteredData} // Export filtered data
                    filename="outward_transactions_report"
                    title="Outward Transaction Report"
                    columns={exportColumns}
                    excelTransform={excelTransform}
                    summary={summary}
                    summaryConfig={summaryConfig}
                />
            </div>

            {/* Summary Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <div className="bg-gradient-to-r from-blue-500 to-blue-600 rounded-xl p-6 text-white">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-blue-100 text-sm font-medium">Total Transactions</p>
                            <p className="text-3xl font-bold">{summary.totalTransactions}</p>
                        </div>
                        <TrendingUp className="w-8 h-8 text-blue-200" />
                    </div>
                </div>

                <div className="bg-gradient-to-r from-emerald-500 to-emerald-600 rounded-xl p-6 text-white">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-emerald-100 text-sm font-medium">Total Quantity</p>
                            <p className="text-3xl font-bold">{summary.totalQuantity}</p>
                        </div>
                        <Package className="w-8 h-8 text-emerald-200" />
                    </div>
                </div>

                {/* <div className="bg-gradient-to-r from-purple-500 to-purple-600 rounded-xl p-6 text-white">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-purple-100 text-sm font-medium">Serial Numbers</p>
                            <p className="text-3xl font-bold">{summary.totalSerialNumbers}</p>
                        </div>
                        <Package className="w-8 h-8 text-purple-200" />
                    </div>
                </div> */}

                <div className="bg-gradient-to-r from-orange-500 to-orange-600 rounded-xl p-6 text-white">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-orange-100 text-sm font-medium">Customers</p>
                            <p className="text-3xl font-bold">{summary.totalFranchises + summary.totalMerchants}</p>
                        </div>
                        <Users className="w-8 h-8 text-orange-200" />
                    </div>
                </div>
            </div>

            {/* Breakdown Cards - same as before */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {/* Customer Type Breakdown */}
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                    <h3 className="text-lg font-semibold text-gray-900 mb-4">Customer Type Distribution</h3>
                    <div className="space-y-3">
                        {Object.entries(summary.customerTypeBreakdown).map(([type, count]) => (
                            <div key={type} className="flex items-center justify-between">
                                <span className="text-sm font-medium text-gray-600">{type}</span>
                                <span className="text-lg font-bold text-gray-900">{count}</span>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Delivery Method Breakdown */}
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                    <h3 className="text-lg font-semibold text-gray-900 mb-4">Delivery Methods</h3>
                    <div className="space-y-3">
                        {Object.entries(summary.deliveryMethodBreakdown).map(([method, count]) => (
                            <div key={method} className="flex items-center justify-between">
                                <span className="text-sm font-medium text-gray-600">{method}</span>
                                <span className="text-lg font-bold text-gray-900">{count}</span>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Serial Status Breakdown */}
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                    <h3 className="text-lg font-semibold text-gray-900 mb-4">Serial Status Distribution</h3>
                    <div className="space-y-3">
                        {Object.entries(summary.serialStatusBreakdown).map(([status, count]) => (
                            <div key={status} className="flex items-center justify-between">
                                <span className="text-sm font-medium text-gray-600">{status}</span>
                                <span className="text-lg font-bold text-gray-900">{count}</span>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* Filters and Search - UPDATED */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
                    <div className="relative">
                        <Search className="w-4 h-4 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                        <input
                            type="text"
                            placeholder="Search all fields..."
                            value={globalFilter ?? ''}
                            onChange={(e) => setGlobalFilter(e.target.value)}
                            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                        />
                    </div>

                    <div>
                        {/* <label className="block text-xs font-medium text-gray-700 mb-1">Start Date</label> */}
                        <input
                            type="date"
                            value={dateRange.startDate}
                            onChange={(e) => setDateRange(prev => ({ ...prev, startDate: e.target.value }))}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                        />
                    </div>

                    <div>
                        {/* <label className="block text-xs font-medium text-gray-700 mb-1">End Date</label> */}
                        <input
                            type="date"
                            value={dateRange.endDate}
                            onChange={(e) => setDateRange(prev => ({ ...prev, endDate: e.target.value }))}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                        />
                    </div>

                    <button
                        onClick={fetchOutwardTransactions}
                        className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors font-medium"
                    >
                        Fetch
                    </button>

                    <button
                        onClick={clearFilters}
                        className="px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors font-medium flex items-center gap-2"
                    >
                        <X className="w-4 h-4" />
                        Clear Filters
                    </button>
                </div>
            </div>

            {/* Table - same as before but will now show filtered data */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-gray-50 border-b border-gray-200">
                            {table.getHeaderGroups().map((headerGroup) => (
                                <tr key={headerGroup.id}>
                                    {headerGroup.headers.map((header) => (
                                        <th
                                            key={header.id}
                                            className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                                            onClick={header.column.getToggleSortingHandler()}
                                        >
                                            <div className="flex items-center gap-2">
                                                {flexRender(header.column.columnDef.header, header.getContext())}
                                                {{
                                                    asc: <ChevronUp className="w-4 h-4" />,
                                                    desc: <ChevronDown className="w-4 h-4" />,
                                                }[header.column.getIsSorted()] ?? null}
                                            </div>
                                        </th>
                                    ))}
                                </tr>
                            ))}
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                            {table.getRowModel().rows.map((row) => (
                                <tr key={row.id} className="hover:bg-gray-50">
                                    {row.getVisibleCells().map((cell) => (
                                        <td key={cell.id} className="px-4 py-3 whitespace-nowrap text-sm">
                                            {flexRender(cell.column.columnDef.cell, cell.getContext())}
                                        </td>
                                    ))}
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

                {/* Pagination - same as before */}
                <div className="bg-gray-50 px-6 py-3 flex items-center justify-between border-t border-gray-200">
                    <div className="flex items-center gap-2">
                        <span className="text-sm text-gray-700">
                            Showing {table.getState().pagination.pageIndex * table.getState().pagination.pageSize + 1} to{' '}
                            {Math.min((table.getState().pagination.pageIndex + 1) * table.getState().pagination.pageSize, table.getFilteredRowModel().rows.length)}{' '}
                            of {table.getFilteredRowModel().rows.length} results
                        </span>
                    </div>
                    
                    <div className="flex items-center gap-2">
                        <button
                            onClick={() => table.previousPage()}
                            disabled={!table.getCanPreviousPage()}
                            className="px-3 py-1 text-sm border border-gray-300 rounded hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            Previous
                        </button>
                        
                        <span className="text-sm text-gray-700">
                            Page {table.getState().pagination.pageIndex + 1} of {table.getPageCount()}
                        </span><button
                            onClick={() => table.nextPage()}
                            disabled={!table.getCanNextPage()}
                            className="px-3 py-1 text-sm border border-gray-300 rounded hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            Next
                        </button>
                        
                        <select
                            value={table.getState().pagination.pageSize}
                            onChange={(e) => table.setPageSize(Number(e.target.value))}
                            className="text-sm border border-gray-300 rounded px-2 py-1"
                        >
                            {[10, 20, 30, 40, 50].map((pageSize) => (
                                <option key={pageSize} value={pageSize}>
                                    Show {pageSize}
                                </option>
                            ))}
                        </select>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default OutwardTransactionReport;
