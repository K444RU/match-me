// src/pages/chats/components/SettingsDialog.tsx
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
  } from "@/components/ui/dialog";
  import { Settings } from "lucide-react";
  import { DropdownMenuItem } from "@/components/ui/dropdown-menu";
import SettingsPage from "@/pages/user-settings/SettingsPage";
  
  const SettingsDialog = () => {
    return (
      <Dialog>
        <DialogTrigger asChild>
          <DropdownMenuItem onSelect={(e) => e.preventDefault()} className="cursor-pointer">
            <Settings />
            Settings
          </DropdownMenuItem>
        </DialogTrigger>
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