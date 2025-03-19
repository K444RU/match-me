import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog';
import { Dispatch, SetStateAction } from 'react';

const RecommendationsDialog = ({
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
                    <DialogTitle>Matching Recommendations</DialogTitle>
                    <DialogDescription>
                        View your latest matching recommendations here.
                    </DialogDescription>
                </DialogHeader>
                <div className="space-y-4">
                    {/* Add your recommendations content here */}
                </div>
            </DialogContent>
        </Dialog>
    );
};

export default RecommendationsDialog;