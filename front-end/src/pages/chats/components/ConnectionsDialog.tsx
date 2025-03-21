import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog';
import { Dispatch, SetStateAction } from 'react';
import ConnectionRequestHandler from './ConnectionRequestHandler';
import ActiveConnections from './ActiveConnections';

const ConnectionsDialog = ({
                               setIsOpen,
                               isOpen,
                           }: {
    setIsOpen: Dispatch<SetStateAction<boolean>>;
    isOpen: boolean;
}) => {
    return (
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogContent className="sm:max-w-[425px]">
                <DialogHeader>
                    <DialogTitle>My Connections</DialogTitle>
                    <DialogDescription>
                        View and manage your connections here.
                    </DialogDescription>
                </DialogHeader>
                <div className="space-y-4">
                    <ConnectionRequestHandler />
                    <ActiveConnections />
                </div>
            </DialogContent>
        </Dialog>
    );
};

export default ConnectionsDialog;