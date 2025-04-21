import { DropdownMenuContent, DropdownMenuItem } from '@/components/ui/dropdown-menu';
import { Star, Users } from 'lucide-react';

interface BlindMenuProps {
  setIsConnectionsModalOpen: (open: boolean) => void;
  setIsRecommendationsModalOpen: (open: boolean) => void;
  setIsDropdownOpen: (open: boolean) => void;
}

const BlindMenu = ({ setIsConnectionsModalOpen, setIsRecommendationsModalOpen, setIsDropdownOpen }: BlindMenuProps) => {
  return (
    <DropdownMenuContent
      className="w-(--radix-dropdown-menu-trigger-width) min-w-56 rounded-lg"
      side="bottom"
      align="start"
      sideOffset={4}
    >
      <DropdownMenuItem
        onSelect={(e) => {
          e.preventDefault();
          setIsConnectionsModalOpen(true);
          setIsDropdownOpen(false);
        }}
      >
        <Users className="mr-2 size-4" />
        <span>Check my current Connections</span>
      </DropdownMenuItem>
      <DropdownMenuItem
        onSelect={(e) => {
          e.preventDefault();
          setIsRecommendationsModalOpen(true);
          setIsDropdownOpen(false);
        }}
      >
        <Star className="mr-2 size-4" />
        <span>Get my new Matching Recommendations</span>
      </DropdownMenuItem>
    </DropdownMenuContent>
  );
};

export default BlindMenu;
