// src/pages/chats/components/SettingsDialog.tsx
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog';
import SettingsPage from '@/pages/user-settings/SettingsPage';
import { Dispatch, SetStateAction } from 'react';

const SettingsDialog = ({
  setIsOpen,isOpen
}: {
  setIsOpen: Dispatch<SetStateAction<boolean>>, isOpen: boolean;
}) => {
    return (
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Settings</DialogTitle>
                    <DialogDescription>
                        Manage your account settings and preferences here.
                    </DialogDescription>
                </DialogHeader>
                <SettingsPage />
            </DialogContent>
        </Dialog>
    );
};

export default SettingsDialog;
