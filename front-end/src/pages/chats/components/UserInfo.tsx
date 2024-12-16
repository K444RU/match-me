import { useAuth } from '@/features/authentication/AuthContext';
import {
    Bell,
    ChevronsUpDown,
    CreditCard,
    LogOut,
    Settings,
    Sparkles,
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
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

const UserInfo = () => {
    // Dunno how to see loading state since we are not awaiting useAuth...
    // const [loading, setLoading] = useState<boolean>(true);
    // const [error, setError] = useState<string | null>(null);
    // Maybe we don't need it anyway

    const { user } = useAuth();
    if (!user) return;

    const navigate = useNavigate();
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
                    <DropdownMenuItem
                        onClick={() => navigate('/settings')}
                        className="cursor-pointer"
                    >
                        <Settings />
                        Settings
                    </DropdownMenuItem>
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
        // <div className="relative flex h-16 w-full items-center justify-between bg-primary-200 px-2 text-text">
        //     <div className='flex items-center'>
        //         <img
        //             src="https://www.pngitem.com/pimgs/m/41-415477_cat-tongue-png-cute-cat-png-transparent-png.png"
        //             alt=""
        //             className="h-12 w-12 rounded-md object-cover"
        //         />
        //         {/* <FaRegUserCircle className="ml-2 mr-2 h-20 w-20 text-text-600" /> */}
        //         <div className="flex flex-col">
        //             <h2 className="ml-2 text-2xl font-bold text-text-700">
        //                 {user?.firstName || (
        //                     <Skeleton className="h-[32px] w-[120px] rounded-md bg-text-300" />
        //                 )}
        //             </h2>
        //             <h2 className="ml-2 text-lg font-bold text-text-700">
        //                 {user?.alias || (
        //                     <Skeleton className="mt-1 h-[28px] w-[100px] rounded-md bg-text-300" />
        //                 )}
        //             </h2>
        //         </div>
        //     </div>
        //     <div className="flex flex-col justify-end gap-2">
        //         <Link to={'/settings'}>
        //             <Settings className="h-8 w-8 rounded-md bg-primary-300  text-primary-50 hover:bg-primary-400" />
        //         </Link>
        //         <Link to={'/logout'}>
        //             <LogOut className="h-8 w-8 rounded-md bg-primary-300  text-primary-50 hover:bg-primary-400" />
        //         </Link>
        //     </div>
        // </div>
    );
};

export default UserInfo;
