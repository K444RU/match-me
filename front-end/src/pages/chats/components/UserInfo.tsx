import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { SidebarMenuButton } from '@/components/ui/sidebar';
import { useAuth } from '@/features/authentication';
import type { User as AuthUser } from '@/features/authentication';
import { ChevronsUpDown, LogOut, Settings, User } from 'lucide-react';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import SettingsDialog from './SettingsDialog';

const UserInfo = () => {
  // Dunno how to see loading state since we are not awaiting useAuth...
  // const [loading, setLoading] = useState<boolean>(true);
  // const [error, setError] = useState<string | null>(null);
  // Maybe we don't need it anyway

  const { user, logout } = useAuth();
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const navigate = useNavigate();
  if (!user) return;

  const profileSrc = user.profilePicture;

  function getInitials(user: AuthUser) {
    if (!user.alias) {
      return user.firstName ? user.firstName.charAt(0).toUpperCase() : user.lastName ? user.lastName.charAt(0).toUpperCase() : 'X';
    } else {
      return user.alias.charAt(0).toUpperCase();
    }
  }

  return (
    <>
      <DropdownMenu open={isDropdownOpen} onOpenChange={setIsDropdownOpen}>
        <DropdownMenuTrigger asChild>
          <div>
            <SidebarMenuButton
              size="lg"
              className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
            >
              <Avatar className="size-8 rounded-lg">
                <AvatarImage src={profileSrc} alt={user.firstName} />
                <AvatarFallback className="rounded-lg">{getInitials(user)}</AvatarFallback>
              </Avatar>
              <div className="grid flex-1 text-left text-sm leading-tight">
                <span className="truncate font-semibold">
                  {user.firstName} {user.lastName}
                </span>
                <span className="truncate text-xs">{user.alias}</span>
              </div>
              <ChevronsUpDown className="ml-auto size-4" />
            </SidebarMenuButton>
          </div>
        </DropdownMenuTrigger>
        <DropdownMenuContent
          className="w-(--radix-dropdown-menu-trigger-width) min-w-56 rounded-lg"
          side="bottom"
          align="end"
          sideOffset={4}
        >
          <DropdownMenuLabel className="p-0 font-normal">
            <div className="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
              <Avatar className="size-8 rounded-lg">
                <AvatarImage src={profileSrc} alt={user.firstName} />
                <AvatarFallback className="rounded-lg text-sidebar-accent-foreground">{getInitials(user)}</AvatarFallback>
              </Avatar>
              <div className="grid flex-1 text-left text-sm leading-tight">
                <span className="truncate font-semibold">
                  {user.firstName} {user.lastName}
                </span>
                <span className="truncate text-xs">{user.alias}</span>
              </div>
            </div>
          </DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuGroup>
            <DropdownMenuItem onSelect={() => navigate('/me')}>
              <User />
              Profile
            </DropdownMenuItem>
            <DropdownMenuItem
              onSelect={(e) => {
                e.preventDefault();
                setIsDialogOpen(true);
                setIsDropdownOpen(false);
              }}
            >
              <Settings />
              Settings
            </DropdownMenuItem>
          </DropdownMenuGroup>
          <DropdownMenuSeparator />
          <DropdownMenuItem
            onSelect={() => {
              logout();
              navigate('/login');
            }}
          >
            <LogOut />
            Log out
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
      <SettingsDialog setIsOpen={setIsDialogOpen} isOpen={isDialogOpen} />
    </>
  );
};

export default UserInfo;
