import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  Server,
  AlertTriangle,
  Terminal,
  GitCommit,
  Bell,
  Zap,
  ShieldCheck,
  Users,
  LogOut,
  Cpu
} from 'lucide-react';

export const Layout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'Services', path: '/services', icon: Server },
    { name: 'Incidents', path: '/incidents', icon: AlertTriangle },
    { name: 'Central Logs', path: '/logs', icon: Terminal },
    { name: 'Deployments', path: '/deployments', icon: GitCommit },
    { name: 'Alert Rules', path: '/alerts', icon: Bell },
    { name: 'AIOps Simulator', path: '/simulator', icon: Zap },
    { name: 'Audit Logs', path: '/audit-logs', icon: ShieldCheck },
  ];

  if (user?.role === 'ROLE_ADMIN') {
    navItems.push({ name: 'User Management', path: '/users', icon: Users });
  }

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen flex bg-[#0B0F19]">
      {/* Sidebar */}
      <aside className="w-64 bg-[#0F172A]/80 border-r border-gray-800 flex flex-col justify-between hidden md:flex">
        <div>
          {/* Logo Brand Header */}
          <div className="h-16 flex items-center gap-3 px-6 border-b border-gray-800/60">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-indigo-600 to-violet-500 flex items-center justify-center shadow-lg shadow-indigo-500/30">
              <Cpu className="w-5 h-5 text-white" />
            </div>
            <div>
              <span className="font-extrabold text-lg text-white tracking-wide">Sentinel<span className="text-indigo-400">Ops</span></span>
              <span className="block text-[10px] text-indigo-400 font-mono tracking-widest uppercase">AIOps Platform</span>
            </div>
          </div>

          {/* Navigation Links */}
          <nav className="p-4 space-y-1.5">
            {navItems.map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === item.path || (item.path !== '/dashboard' && location.pathname.startsWith(item.path));
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  className={`flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-sm font-medium transition-all ${
                    isActive
                      ? 'bg-indigo-600/15 text-indigo-400 border border-indigo-500/30 shadow-sm'
                      : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
                  }`}
                >
                  <Icon className={`w-4 h-4 ${isActive ? 'text-indigo-400' : 'text-gray-400'}`} />
                  {item.name}
                </Link>
              );
            })}
          </nav>
        </div>

        {/* User Footer Card */}
        <div className="p-4 border-t border-gray-800/60">
          <div className="flex items-center justify-between p-2 rounded-lg bg-gray-900/60 border border-gray-800">
            <div className="truncate">
              <p className="text-sm font-semibold text-gray-200 truncate">{user?.fullName}</p>
              <span className="inline-block text-[10px] font-mono uppercase text-indigo-400 font-bold bg-indigo-500/10 px-1.5 py-0.5 rounded border border-indigo-500/20">
                {user?.role?.replace('ROLE_', '')}
              </span>
            </div>
            <button
              onClick={handleLogout}
              className="p-2 text-gray-400 hover:text-rose-400 hover:bg-rose-500/10 rounded-lg transition-colors"
              title="Logout"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>
        </div>
      </aside>

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0">
        {/* Top Header Navbar */}
        <header className="h-16 bg-[#0F172A]/50 border-b border-gray-800/60 px-6 flex items-center justify-between backdrop-blur-md sticky top-0 z-40">
          <div className="flex items-center gap-2">
            <span className="text-xs text-gray-500">System Status:</span>
            <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-xs font-medium bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-ping"></span>
              Operational
            </span>
          </div>

          <div className="flex items-center gap-4">
            <span className="text-xs text-gray-400 font-mono hidden sm:inline">User: <strong className="text-gray-200">{user?.email}</strong></span>
            <button
              onClick={handleLogout}
              className="flex items-center gap-1.5 text-xs text-rose-400 hover:text-rose-300 font-medium px-3 py-1.5 rounded-lg border border-rose-500/20 bg-rose-500/5 hover:bg-rose-500/10 transition-colors"
            >
              <LogOut className="w-3.5 h-3.5" />
              Sign Out
            </button>
          </div>
        </header>

        {/* Page Content */}
        <main className="flex-1 p-6 md:p-8 overflow-y-auto">
          {children}
        </main>
      </div>
    </div>
  );
};
