import { Dispatch, SetStateAction } from 'react';
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogDescription,
} from '@/components/ui/dialog';
import { useConnections } from '@/pages/chats/components/ConnectionContext';
import ConnectionRequestHandler from '@/pages/chats/components/ConnectionRequestHandler';
import ActiveConnections from '@/pages/chats/components/ActiveConnections';

interface Props {
    setIsOpen: Dispatch<SetStateAction<boolean>>;
    isOpen: boolean;
}

const ConnectionsDialog = ({ setIsOpen, isOpen }: Props) => {
    const { connections } = useConnections();

    return (
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogContent className="sm:max-w-[425px]">
                <DialogHeader>
                    <DialogTitle>My Connections</DialogTitle>
                    <DialogDescription>View and manage your connections here.</DialogDescription>
                </DialogHeader>
                <div className="space-y-4">
                    <ConnectionRequestHandler pendingIncoming={connections.pendingIncoming} />
                    <ActiveConnections active={connections.active} />
                </div>
            </DialogContent>
        </Dialog>
    );
};

export default ConnectionsDialog;
