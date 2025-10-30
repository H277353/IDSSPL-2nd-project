
import React, { useState, useEffect, useMemo, useRef } from 'react';
import { Link, useLocation } from 'react-router';
import {
  Home,
  Users,
  Package,
  DollarSign,
  UserPlus,
  ArrowDown,
  Calculator,
  Upload,
  ChevronDown,
  ChevronRight,
  Menu,
  X,
  BarChart3,
  CreditCard,
  Store,
  Banknote,
  Coins,
  Eye,
  Package2,
  IndianRupee,
  Ticket
} from 'lucide-react';
import { flattenPermissions } from "./permissionHelper";
import logoImage from '../../assets/SD-2.jpg';


// Reusable Menu Item Component
const MenuItem = React.memo(({
  item,
  isActive,
  isParentActive,
  sidebarCollapsed,
  onMenuClick
}) => {
  const location = useLocation();

  const isActiveLink = (path) => location.pathname === path;

  if (item.children) {
    return (
      <div>
        <button
          onClick={() => onMenuClick(item.key)}
          className={`w-full flex items-center justify-between px-3 py-3 rounded-xl transition-all duration-200 hover:bg-blue-100 group ${isParentActive ? 'bg-blue-100 border border-blue-500' : ''
            }`}
        >
          <div className="flex items-center space-x-3">
            <div className={`p-2 rounded-lg ${isParentActive ? 'bg-blue-100' : 'bg-gray-100 group-hover:bg-gray-200'
              } transition-colors duration-200`}>
              <item.icon className={`w-5 h-5 ${isParentActive ? 'text-blue-600' : item.iconColor || 'text-gray-600'
                }`} />
            </div>
            {!sidebarCollapsed && (
              <span className={`font-medium ${isParentActive ? 'text-blue-900' : 'text-gray-700'
                }`}>
                {item.title}
              </span>
            )}
          </div>
          {!sidebarCollapsed && (
            <div className="transition-transform duration-200">
              {isActive ? (
                <ChevronDown className="w-4 h-4 text-gray-600" />
              ) : (
                <ChevronRight className="w-4 h-4 text-gray-600" />
              )}
            </div>
          )}
        </button>

        {isActive && !sidebarCollapsed && (
          <div className="ml-6 mt-2 space-y-1">
            {item.children.map((child) => (
              <Link
                key={child.path}
                to={child.path}
                className={`flex items-center space-x-3 px-3 py-2 rounded-lg transition-all duration-200 ${isActiveLink(child.path)
                    ? 'bg-gray-500 text-white shadow-md'
                    : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'
                  }`}
              >
                <child.icon className="w-4 h-4" />
                <span className="text-sm font-medium">{child.title}</span>
              </Link>
            ))}
          </div>
        )}
      </div>
    );
  }

  return (
    <Link
      to={item.path}
      className={`flex items-center space-x-3 px-3 py-3 rounded-xl transition-all duration-200 hover:bg-gray-50 group ${isActiveLink(item.path) ? 'bg-blue-50 border border-blue-200' : ''
        }`}
    >
      <div className={`p-2 rounded-lg ${isActiveLink(item.path) ? 'bg-blue-100' : 'bg-gray-100 group-hover:bg-gray-200'
        } transition-colors duration-200`}>
        <item.icon className={`w-5 h-5 ${isActiveLink(item.path) ? 'text-blue-600' : item.iconColor || 'text-gray-600'
          }`} />
      </div>
      {!sidebarCollapsed && (
        <span className={`font-medium ${isActiveLink(item.path) ? 'text-blue-900' : 'text-gray-700'
          }`}>
          {item.title}
        </span>
      )}
    </Link>
  );
});

MenuItem.displayName = 'MenuItem';

// Reusable Sidebar Header Component
const SidebarHeader = React.memo(({ sidebarCollapsed, onToggle, userType }) => {
  const USER_TYPE_DISPLAY = {
    admin: 'Admin Panel',
    super_admin: 'Admin Panel',
    franchise: 'Franchise Portal',
    merchant: 'Merchant Portal',
    default: 'Management System'
  };

  const displayText = USER_TYPE_DISPLAY[userType] || USER_TYPE_DISPLAY.default;

  return (
    <div className="flex items-center justify-between p-4 bg-gray-200 ">
      {!sidebarCollapsed && (
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-xl flex items-center justify-center shadow-lg">
            <img 
              src={logoImage} 
              alt="Same Day Solution Logo" 
              className="w-[55px] max-w-md mx-auto rounded-xl p-1 bg-gray-500  "
             />
          </div>
          <div>
            <h1 className="text-xs font-bold text-gray-800">Merchant Management System</h1>
            <p className="text-xs text-gray-500">{displayText}</p>
          </div>
        </div>
      )}
      <button
        onClick={onToggle}
        className="p-2 rounded-lg hover:bg-gray-100 transition-colors duration-200"
      >
        {sidebarCollapsed ? (
          <Menu className="w-5 h-5 text-gray-600" />
        ) : (
          <X className="w-5 h-5 text-gray-600" />
        )}
      </button>
    </div>
  );
});

SidebarHeader.displayName = 'SidebarHeader';

// Permission checker function
const hasPermission = (permissionSet, permissionName) => {
  return permissionSet.has(permissionName);
};

// Filter menu items based on permissions
const filterMenuItemsByPermissions = (menuItems, permissionSet, userType) => {
  return menuItems.reduce((acc, item) => {
    // Always show Dashboard parent for all admin types
    if (item.key === 'dashboard') {
      if (item.children) {
        const filteredChildren = item.children.filter(child => {
          // Always show the main Dashboard child for all admins
          if (child.title === 'DashBoard') return true;

          // Show Admin Management and Logs only for super_admin
          if (child.title === 'Admin Management' || child.title === 'Logs' || child.title === 'Taxes Manage' || child.title === 'Edit History') {
            return userType === 'super_admin';
          }

          // Show Wallet Adjustment only for super admin
          if (child.title === 'Wallet Adjustment') {
            return userType === 'super_admin';
          }

          // Show My Permissions only for regular admin
          if (child.title === 'My Permissions') {
            return userType === 'admin';
          }

          return hasPermission(permissionSet, child.permission || child.title);
        });

        if (filteredChildren.length > 0) {
          acc.push({ ...item, children: filteredChildren });
        }
      } else {
        acc.push(item);
      }
      return acc;
    }

    // For other menu items, apply normal permission filtering
    // For other menu items, apply normal permission filtering
    if (item.children) {
      const filteredChildren = item.children.filter(child => {
        // Skip permission check for Reports children
        if (item.key === 'reports') return true;

        return hasPermission(permissionSet, child.permission || child.title);
      });

      if (filteredChildren.length > 0) {
        acc.push({ ...item, children: filteredChildren });
      }
      return acc;
    }

    // For items without children, check their permission
    if (hasPermission(permissionSet, item.permission || item.title)) {
      acc.push(item);
    }

    return acc;
  }, []);
};

// Menu configurations
const MENU_CONFIGS = {
  admin: (userType) => {
    const getDashboardChildren = () => {
      const children = [
        { title: 'DashBoard', path: '/dashboard', icon: Users, permission: 'Dashboard' }
      ];

      if (userType === 'super_admin') {
        children.push(
          { title: 'Admin Management', path: '/dashboard/role-management', icon: Users, permission: 'Admin Management' },
          { title: 'Logs', path: '/dashboard/logs', icon: Users, permission: 'Logs' },
          { title: 'Taxes Manage', path: '/dashboard/taxes-management', icon: IndianRupee, permission: 'Taxes Management' },
          { title: 'Edit History', path: '/dashboard/edit-history', icon: Users, permission: 'Edit History' },
          { title: 'Wallet Adjustment', path: '/dashboard/wallet-adjustment', icon: Users, permission: 'Wallet Adjustment' }
        );
      } else if (userType === 'admin') {
        children.push(
          { title: 'My Permissions', path: '/dashboard/role-management', icon: Users, permission: 'My Permissions' }
        );
      }

      return children;
    };

    return [
      {
        title: 'Dashboard',
        key: "dashboard",
        icon: Home,
        iconColor: '',
        permission: 'Dashboard',
        children: getDashboardChildren()
      },
      {
        title: 'Vendors',
        key: 'vendors',
        icon: Users,
        iconColor: '',
        permission: 'Vendors',
        children: [
          { title: 'Vendor List', path: '/dashboard/vendors', icon: Users, permission: 'Vendor List' },
          { title: 'Product List', path: '/dashboard/inventory', icon: Package, permission: 'Product List' },
          { title: 'Vendor Rates', path: '/dashboard/vendors/rates', icon: DollarSign, permission: 'Vendor Rates' }
        ]
      },
      {
        title: 'Inventory',
        key: 'inventory',
        icon: Package,
        iconColor: '',
        permission: 'Inventory Management',
        children: [
          { title: 'Pricing Scheme', path: '/dashboard/inventory/pricing', icon: Calculator, permission: 'Pricing Scheme' },
          { title: 'Product Scheme Assign', path: '/dashboard/inventory/products-assign', icon: Calculator, permission: 'Product Scheme Assign' },
          { title: 'Inventory', path: '/dashboard/inventory/inventory', icon: ArrowDown, permission: 'Inventory' },
        ]
      },
      {
        title: 'Customers',
        key: 'customers',
        icon: Users,
        iconColor: '',
        permission: 'Customers',
        children: [
          { title: 'Customer List', path: '/dashboard/customers', icon: Users, permission: 'Customer List' },
          { title: 'Onboard Customer', path: '/dashboard/customers/onboard', icon: UserPlus, permission: 'Onboard Customer' },
          { title: 'Merchant Approval', path: '/dashboard/customers/admin-approval', icon: UserPlus, permission: 'Merchant Approval' },
          { title: 'Products Distribution', path: '/dashboard/customers/products-distribution', icon: Package, permission: 'Product Distribution' }
        ]
      },
      {
        title: 'Other',
        key: 'other',
        icon: CreditCard,
        iconColor: '',
        permission: 'Other',
        children: [
          { title: 'File Upload', path: '/dashboard/others/upload', icon: Upload, permission: 'File Upload' },
          { title: 'Charge Calculation', path: '/dashboard/others/charges', icon: Calculator, permission: 'Charge Calculation' },
          { title: 'Batch Status', path: '/dashboard/others/batch-status', icon: Eye, permission: 'Batch Status' }
        ]
      },
      {
        title: 'Reports',
        path: '/dashboard/reports',
        key: "reports",
        icon: BarChart3,
        iconColor: '',
        permission: 'Reports',
        children: [
          { title: 'Franchise Reports', path: '/dashboard/reports/franchise', icon: BarChart3},
          { title: 'Merchant Reports', path: '/dashboard/reports/merchant', icon: BarChart3},
          { title: 'Vendor Reports', path: '/dashboard/reports/vendor', icon: BarChart3},
          { title: 'Merchant Transaction Reports', path: '/dashboard/reports/merchant-transactions', icon: BarChart3},
          { title: 'Franchise Transaction Report', path: '/dashboard/reports/franchise-transactions', icon: BarChart3},
          { title: 'Inward Report', path: '/dashboard/reports/inward', icon: BarChart3},
          { title: 'Outward Report', path: '/dashboard/reports/outward', icon: BarChart3 },
          { title: 'Return Report', path: '/dashboard/reports/return', icon: BarChart3},
          { title: 'Product Reports', path: '/dashboard/reports/product', icon: BarChart3},
          { title: 'Stock Reports', path: '/dashboard/reports/stock', icon: BarChart3}

        ]
      }
    ];
  },
  franchise: [
    {
      title: 'Dashboard',
      path: '/dashboard',
      icon: Home,
      key: 'dashboard',
      iconColor: ''
    },
    {
      title: 'Merchants',
      key: 'merchants',
      icon: Store,
      iconColor: '',
      children: [
        { title: 'Merchant List', path: '/dashboard/merchants', icon: Store }
      ]
    },
    {
      title: 'Inventory',
      key: 'inventory',
      icon: Package,
      iconColor: '',
      children: [
        { title: 'Inward Entry', path: '/dashboard/customers/inward-products', icon: Package },
        { title: 'Product List', path: '/dashboard/inventory/customer-products', icon: Package },
        { title: 'Product Distribution', path: '/dashboard/customers/products-distribution', icon: Package }
      ]
    },
    {
      title: 'Payout',
      key: 'payout',
      path: '/dashboard/payout',
      icon: Coins,
      iconColor: '',
    },
    
    {
      title: 'Reports',
      path: '/dashboard/reports',
      key: 'reports',
      icon: BarChart3,
      iconColor: '',
      children: [
        { title: 'Merchant Transaction Reports', path: '/dashboard/reports/merchant-transactions', icon: BarChart3},
        { title: 'Franchise Transaction Report', path: '/dashboard/reports/franchise-transactions', icon: BarChart3},
      ]
    }
  ],
  merchant: [
    {
      title: 'Dashboard',
      path: '/dashboard',
      key: 'dashboard',
      icon: Home,
      iconColor: ''
    },
    {
      title: 'Inventory',
      key: 'inventory',
      icon: Package,
      iconColor: '',
      children: [
        { title: 'Inward Entry', path: '/dashboard/customers/inward-products', icon: Package },
        { title: 'Product List', path: '/dashboard/inventory/customer-products', icon: Package }
      ]
    },
    {
      title: 'Bill Payment',
      key: 'payment',
      path: '/dashboard/credit-card-bill-payment',
      icon: Banknote,
      iconColor: ''
    },
    {
      title: 'Payout',
      key: 'payout',
      path: '/dashboard/payout',
      icon: Coins,
      iconColor: '',
    },
    
    {
      title: 'Card Details',
      key: 'card-details',
      icon: CreditCard,
      iconColor: ''
    },
    {
      title: 'Reports',
      path: '/dashboard/reports',
      key: 'reports',
      icon: BarChart3,
      iconColor: '',
      children: [
        { title: 'Merchant Transaction Reports', path: '/dashboard/reports/merchant-transactions', icon: BarChart3}
      ]
    }
  ]
};

const getMenuItems = (userType) => {
  const normalizedUserType = userType === "super_admin" ? "admin" : userType;

  if (normalizedUserType === 'admin') {
    return MENU_CONFIGS.admin(userType);
  }

  return MENU_CONFIGS[normalizedUserType] || MENU_CONFIGS.merchant;
};


// Main Sidebar Component
const Sidebar = ({ userType }) => {
  const location = useLocation();
  const sidebarRef = useRef(null);

  const [sidebarCollapsed, setSidebarCollapsed] = useState(true);
  const [expandedMenus, setExpandedMenus] = useState({});

  // Get permissions from localStorage and create permission set
  const permissionSet = useMemo(() => {
    // For super admin, grant all permissions
    if (userType === 'super_admin') {
      return new Set([
        'Dashboard', 'Admin Management', 'Logs','Taxes Manage', 'Edit History', 'Wallet Adjustment',
        'Vendors', 'Vendor List', 'Product List', 'Vendor Rates',
        'Inventory', 'Pricing Scheme', 'Product Scheme Assign', 'Inventory Management',
        'Customers', 'Customer List', 'Onboard Customer', 'Merchant Approval', 'Product Distribution',
        'Other', 'File Upload', 'Charge Calculation', 'Batch Status',
        'Reports'
      ]);
    }

    // Only apply permission filtering for regular admin users
    if (userType === 'admin') {
      try {
        const storedPermissions = localStorage.getItem('permissions');
        if (storedPermissions) {
          const permissions = JSON.parse(storedPermissions);
          return flattenPermissions(permissions);
        }
        return new Set(['Dashboard']);
      } catch (error) {
        console.error('Error parsing permissions from localStorage:', error);
        return new Set(['Dashboard']);
      }
    }

    // For non-admin users, return empty set (no filtering)
    return new Set();
  }, [userType]);

  // Handle mouse leave to close sidebar (keep expandedMenus state)
  const handleMouseLeave = () => {
    setSidebarCollapsed(true);
  };

  // Handle mouse enter to open sidebar
  const handleMouseEnter = () => {
    setSidebarCollapsed(false);
  };

  const toggleMenu = (menuKey) => {
    setExpandedMenus(prev => ({
      ...prev,
      [menuKey]: !prev[menuKey]
    }));
  };

  const toggleSidebar = () => {
    setSidebarCollapsed(prev => !prev);

    // If collapsing the sidebar, close all expanded menus
    if (!sidebarCollapsed) {
      setExpandedMenus({});
    }
  };

  const handleMenuClick = (menuKey) => {
    if (sidebarCollapsed) {
      setSidebarCollapsed(false);
      setExpandedMenus(prev => ({
        ...prev,
        [menuKey]: true
      }));
    } else {
      toggleMenu(menuKey);
    }
  };

  const isActiveParent = (children) => {
    return children?.some(child => location.pathname === child.path);
  };

  // Get and filter menu items
  const menuItems = useMemo(() => {
    let items = getMenuItems(userType);

    // Apply permission filtering for admin types
    if (userType === 'admin' || userType === 'super_admin') {
      items = filterMenuItemsByPermissions(items, permissionSet, userType);
    }

    return items;
  }, [userType, permissionSet]);

  return (
    <div
      ref={sidebarRef}

      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      className={`${sidebarCollapsed ? 'w-16' : 'w-64'} bg-gray-200 shadow-xl transition-[width] duration-300 ease-out flex flex-col border-r border-gray-200 h-full overflow-hidden`}
      
    >
      <div className="w-64 flex flex-col h-full">
        <SidebarHeader
          sidebarCollapsed={sidebarCollapsed}
          onToggle={toggleSidebar}
          userType={userType}
        />

        <nav className="flex-1 overflow-y-auto py-4">
          <div className="space-y-2">
            {menuItems.length > 0 ? (
              menuItems.map((item) => (
                <div key={item.key || item.path}>
                  <MenuItem
                    item={item}
                    isActive={expandedMenus[item.key]}
                    isParentActive={isActiveParent(item.children)}
                    sidebarCollapsed={sidebarCollapsed}
                    onMenuClick={handleMenuClick}
                  />
                </div>
              ))
            ) : (
              <div className="px-4 py-2 text-gray-500 text-sm">
                {!sidebarCollapsed && "No permissions available"}
              </div>
            )}
          </div>
        </nav>
      </div>
    </div>
  );
};

export default Sidebar;