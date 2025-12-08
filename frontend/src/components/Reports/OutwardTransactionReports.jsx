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
    Truck,
    Users,
    X,
    Download
} from 'lucide-react';
import { format } from 'date-fns';
import { getAllOutwardTransactions } from '../../constants/API/OutwardTransAPI';
import api from '../../constants/API/axiosInstance';

const OutwardTransactionReport = () => {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(false);
    const [exportingFull, setExportingFull] = useState(false);
    const [error, setError] = useState(null);
    const [globalFilter, setGlobalFilter] = useState('');
    const [dateRange, setDateRange] = useState({
        startDate: '',
        endDate: ''
    });

    // Pagination state
    const [pageIndex, setPageIndex] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalElements, setTotalElements] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    // Fetch paginated data for display
    const fetchTransactions = async (page = 0) => {
        setLoading(true);
        setError(null);

        try {
            const sortParam = ["id", "desc"];

            const params = {
                page,
                size: pageSize,
                sort: sortParam
            };

            // Add date filters only if selected
            if (dateRange.startDate) {
                params.startDate = `${dateRange.startDate}T00:00:00`;
            }
            if (dateRange.endDate) {
                params.endDate = `${dateRange.endDate}T23:59:59`;
            }

            const response = await api.get("/outward-transactions", { params });

            // Transform data for serials same as before...
            const transformedData = [];
            (response.data.content || []).forEach(transaction => {
                if (transaction.serialNumbers && transaction.serialNumbers.length > 0) {
                    transaction.serialNumbers.forEach(serial => {
                        transformedData.push({
                            ...transaction,
                            id: `${transaction.id}`,
                            serialId: serial.id,
                            sid: serial.sid,
                            mid: serial.mid,
                            tid: serial.tid,
                            vpaid: serial.vpaid
                        });
                    });
                } else {
                    transformedData.push({
                        ...transaction,
                        id: transaction.id,
                        serialId: null,
                        sid: "-",
                        mid: "-",
                        tid: "-",
                        vpaid: "-"
                    });
                }
            });

            setData(transformedData);
            setTotalElements(response.data.totalElements);
            setTotalPages(response.data.totalPages);
            setPageIndex(page);
        } catch (error) {
            console.error("Error fetching outward transactions:", error);
            setError("Failed to fetch outward transactions. Please try again.");
        } finally {
            setLoading(false);
        }
    };


    // Export full report to Excel (backend streams all data)
    const exportFullReport = async () => {
        setExportingFull(true);
        try {
            const params = {};
            if (dateRange.startDate) params.startDate = dateRange.startDate;
            if (dateRange.endDate) params.endDate = dateRange.endDate;

            const response = await api.get('/outward-transactions/export', {
                params,
                responseType: 'blob',
            });

            const blob = new Blob([response.data], {
                type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
            });

            const downloadUrl = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = downloadUrl;

            const disposition = response.headers['content-disposition'];
            const filename = disposition
                ? disposition.split('filename=')[1].replace(/"/g, '')
                : `outward_transactions_${dateRange.startDate || 'all'}_to_${dateRange.endDate || 'all'}.xlsx`;

            link.setAttribute('download', filename);
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(downloadUrl);
        } catch (error) {
            console.error('Error exporting full report:', error);
            alert('Error exporting full report');
        } finally {
            setExportingFull(false);
        }
    };

    useEffect(() => {
        fetchTransactions(0);
    }, []);

    // Filter data based on date range (client-side filtering)
    const filteredData = data

    const clearFilters = () => {
        setGlobalFilter('');
        setDateRange({ startDate: '', endDate: '' });
    };

    const columns = useMemo(() => [
        {
            header: 'Transaction ID',
            accessorKey: 'id',
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
            header: 'Quantity',
            accessorKey: 'quantity',
            cell: ({ getValue }) => (
                <span className="font-semibold">{getValue()}</span>
            )
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

    const summary = useMemo(() => {
        const uniqueTransactions = [...new Set(filteredData.map(item => item.transactionId))];
        const uniqueTransactionData = uniqueTransactions.map(transactionId =>
            filteredData.find(item => item.transactionId === transactionId)
        );

        const totalSerials = filteredData.filter(item => item.serialId).length;
        const uniqueFranchises = [...new Set(filteredData.filter(item => item.franchiseName).map(item => item.franchiseName))];
        const uniqueMerchants = [...new Set(filteredData.filter(item => item.merchantName).map(item => item.merchantName))];

        const totalQuantity = uniqueTransactionData.reduce((sum, item) => sum + (item.quantity || 0), 0);

        return {
            totalTransactions: totalElements, // Use backend total
            totalSerialNumbers: totalSerials,
            totalFranchises: uniqueFranchises.length,
            totalMerchants: uniqueMerchants.length,
            totalQuantity: totalQuantity,
        };
    }, [filteredData, totalElements]);

    const table = useReactTable({
        data: filteredData,
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

    const handlePageChange = (newPage) => {
        fetchTransactions(newPage);
    };

    if (loading && data.length === 0) {
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
                        onClick={() => fetchTransactions(0)}
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
                <button
                    onClick={exportFullReport}
                    disabled={exportingFull}
                    className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                >
                    <Download className="w-4 h-4" />
                    {exportingFull ? 'Exporting...' : 'Export Full Report'}
                </button>
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

                <div className="bg-gradient-to-r from-purple-500 to-purple-600 rounded-xl p-6 text-white">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-purple-100 text-sm font-medium">Serial Numbers</p>
                            <p className="text-3xl font-bold">{summary.totalSerialNumbers}</p>
                        </div>
                        <Package className="w-8 h-8 text-purple-200" />
                    </div>
                </div>

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

            {/* Filters */}
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
                        <input
                            type="date"
                            value={dateRange.startDate}
                            onChange={(e) => setDateRange(prev => ({ ...prev, startDate: e.target.value }))}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                        />
                    </div>

                    <div>
                        <input
                            type="date"
                            value={dateRange.endDate}
                            onChange={(e) => setDateRange(prev => ({ ...prev, endDate: e.target.value }))}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                        />
                    </div>

                    <button
                        onClick={() => fetchTransactions(0)}
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

            {/* Table */}
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

                {/* Pagination */}
                <div className="bg-gray-50 px-6 py-3 flex items-center justify-between border-t border-gray-200">
                    <div className="text-sm text-gray-700">
                        Showing page {pageIndex + 1} of {totalPages} ({totalElements} total entries)
                    </div>

                    <div className="flex items-center gap-2">
                        <button
                            onClick={() => handlePageChange(pageIndex - 1)}
                            disabled={pageIndex === 0}
                            className="px-3 py-1 text-sm border border-gray-300 rounded hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            Previous
                        </button>

                        <span className="text-sm text-gray-700">
                            Page {pageIndex + 1} of {totalPages}
                        </span>

                        <button
                            onClick={() => handlePageChange(pageIndex + 1)}
                            disabled={pageIndex >= totalPages - 1}
                            className="px-3 py-1 text-sm border border-gray-300 rounded hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            Next
                        </button>

                        <select
                            value={pageSize}
                            onChange={(e) => {
                                setPageSize(Number(e.target.value));
                                fetchTransactions(0);
                            }}
                            className="text-sm border border-gray-300 rounded px-2 py-1"
                        >
                            {[10, 20, 30, 40, 50].map((size) => (
                                <option key={size} value={size}>
                                    Show {size}
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