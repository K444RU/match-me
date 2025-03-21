import { Button } from '@/components/ui/button';
import { DropdownMenu, DropdownMenuTrigger } from '@/components/ui/dropdown-menu';
import { cn } from '@/lib/utils';
import { ChevronDown } from 'lucide-react';
import { useReducer } from 'react';
import BlindMenu from './chats/components/BlindMenu';
import ConnectionsDialog from './chats/components/ConnectionsDialog';
import RecommendationsDialog from './chats/components/RecommendationsDialog';

type State = {
  recommendationsOpen: boolean;
  connectionsOpen: boolean;
  dropdownOpen: boolean;
};

type ModalKey = keyof State;

type Action = {
  type: 'SET_MODAL';
  modal: ModalKey;
  value: boolean;
};

const initialState: State = {
  recommendationsOpen: false,
  connectionsOpen: false,
  dropdownOpen: false,
};

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'SET_MODAL':
      return { ...state, [action.modal]: action.value };
    default:
      return state;
  }
}

const PreviewPage = () => {
  const [state, dispatch] = useReducer(reducer, initialState);

  return (
    <div className="p-6">
      <DropdownMenu
        open={state.dropdownOpen}
        onOpenChange={(isOpen: boolean) => dispatch({ type: 'SET_MODAL', modal: 'dropdownOpen', value: isOpen })}
      >
        <DropdownMenuTrigger asChild>
          <Button
            size="lg"
            className={cn(
              'justify-between group-data-[collapsible=icon]:hidden',
              'data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground'
            )}
          >
            <span>Blind</span>
            <ChevronDown className="ml-auto size-4" />
          </Button>
        </DropdownMenuTrigger>
        <BlindMenu
          setIsConnectionsModalOpen={(isOpen: boolean) =>
            dispatch({ type: 'SET_MODAL', modal: 'connectionsOpen', value: isOpen })
          }
          setIsRecommendationsModalOpen={(isOpen: boolean) =>
            dispatch({ type: 'SET_MODAL', modal: 'recommendationsOpen', value: isOpen })
          }
          setIsDropdownOpen={(isOpen: boolean) => dispatch({ type: 'SET_MODAL', modal: 'dropdownOpen', value: isOpen })}
        />
      </DropdownMenu>

      <ConnectionsDialog
        isOpen={state.connectionsOpen}
        // @ts-expect-error: dont care
        setIsOpen={(isOpen: boolean) => dispatch({ type: 'SET_MODAL', modal: 'connectionsOpen', value: isOpen })}
      />
      <RecommendationsDialog
        isOpen={state.recommendationsOpen}
        // @ts-expect-error: dont care
        setIsOpen={(isOpen: boolean) => dispatch({ type: 'SET_MODAL', modal: 'recommendationsOpen', value: isOpen })}
      />
    </div>
  );
};

export default PreviewPage;
