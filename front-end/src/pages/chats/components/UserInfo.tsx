import { useAuth } from '@/features/authentication/AuthContext';
import {
    Bell,
    ChevronsUpDown,
    CreditCard,
    LogOut,
    Sparkles,
} from 'lucide-react';
import { SidebarMenuButton } from '@/components/ui/sidebar';
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import SettingsDialog from './SettingsDialog';

const UserInfo = () => {
    // Dunno how to see loading state since we are not awaiting useAuth...
    // const [loading, setLoading] = useState<boolean>(true);
    // const [error, setError] = useState<string | null>(null);
    // Maybe we don't need it anyway

    const { user } = useAuth();
    if (!user) return;

    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <SidebarMenuButton
                    size="lg"
                    className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
                >
                    <Avatar className="h-8 w-8 rounded-lg">
                        <AvatarImage
                            src="https://www.pngitem.com/pimgs/m/41-415477_cat-tongue-png-cute-cat-png-transparent-png.png"
                            alt={user.firstName}
                        />
                        <AvatarFallback className="rounded-lg">
                            CN
                        </AvatarFallback>
                    </Avatar>
                    <div className="grid flex-1 text-left text-sm leading-tight">
                        <span className="truncate font-semibold">
                            {user.firstName} {user.lastName}
                        </span>
                        <span className="truncate text-xs">{user.alias}</span>
                    </div>
                    <ChevronsUpDown className="ml-auto size-4" />
                </SidebarMenuButton>
            </DropdownMenuTrigger>
            <DropdownMenuContent
                className="w-[--radix-dropdown-menu-trigger-width] min-w-56 rounded-lg"
                side="bottom"
                align="end"
                sideOffset={4}
            >
                <DropdownMenuLabel className="p-0 font-normal">
                    <div className="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
                        <Avatar className="h-8 w-8 rounded-lg">
                            <AvatarImage
                                src="https://www.pngitem.com/pimgs/m/41-415477_cat-tongue-png-cute-cat-png-transparent-png.png"
                                alt={user.firstName}
                            />
                            <AvatarFallback className="rounded-lg">
                                CN
                            </AvatarFallback>
                        </Avatar>
                        <div className="grid flex-1 text-left text-sm leading-tight">
                            <span className="truncate font-semibold">
                                {user.firstName} {user.lastName}
                            </span>
                            <span className="truncate text-xs">
                                {user.alias}
                            </span>
                        </div>
                    </div>
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuGroup>
                    <DropdownMenuItem>
                        <Sparkles />
                        Upgrade to Pro
                    </DropdownMenuItem>
                </DropdownMenuGroup>
                <DropdownMenuSeparator />
                <DropdownMenuGroup>
                    <SettingsDialog />
                    <DropdownMenuItem className="cursor-not-allowed bg-muted hover:bg-muted">
                        <CreditCard />
                        Billing
                    </DropdownMenuItem>
                    <DropdownMenuItem className="cursor-not-allowed bg-muted hover:bg-muted">
                        <Bell />
                        Notifications
                    </DropdownMenuItem>
                </DropdownMenuGroup>
                <DropdownMenuSeparator />
                <DropdownMenuItem>
                    <LogOut />
                    Log out
                </DropdownMenuItem>
            </DropdownMenuContent>
        </DropdownMenu>
    );
};

export default UserInfo;
