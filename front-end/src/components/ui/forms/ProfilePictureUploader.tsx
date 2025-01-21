import React, { useState, useRef } from 'react';
import ReactAvatarEditor from 'react-avatar-editor';
import OneHandleSlider from '@ui/forms/OneHandleSlider.tsx';
import { userService } from '@/features/user';

interface ProfilePictureUploaderProps {
    onUploadSuccess?: () => void;
}

const ProfilePictureUploader: React.FC<ProfilePictureUploaderProps> = ({
    onUploadSuccess,
}) => {
    const [image, setImage] = useState<File | string | null>(null);
    const [scale, setScale] = useState<number | null>(1);
    const [position, setPosition] = useState<{ x: number; y: number }>({
        x: 0.5,
        y: 0.5,
    });
    const editorRef = useRef<ReactAvatarEditor>(null);

    const handleNewImage = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) {
            setImage(file);
        }
    };

    const handlePositionChange = (pos: { x: number; y: number }) => {
        setPosition(pos);
    };

    const handleSubmit = async () => {
        if (editorRef.current) {
            const canvas = editorRef.current.getImageScaledToCanvas();
            const base64Image = canvas.toDataURL();

            try {
                const token = localStorage.getItem('authToken') || '';
                await userService.updateProfilePicture(
                    { base64Image },
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                            'Content-Type': 'application/json',
                        },
                    }
                );
                alert('Profile picture uploaded!');
                if (onUploadSuccess) onUploadSuccess();
            } catch (error) {
                console.error('Upload error:', error);
                alert('Failed to upload picture.');
            }
        }
    };

    return (
        <div className="flex items-center gap-6 rounded-lg bg-gray-100 p-6 shadow-md">
            {/* Left: Image Preview */}
            <div className="flex flex-col items-center">
                {image ? (
                    <div className="flex flex-col items-center">
                        <div className="flex size-48 items-center justify-center rounded-lg bg-gray-200">
                            <ReactAvatarEditor
                                ref={editorRef}
                                image={image}
                                width={200}
                                height={200}
                                border={0}
                                borderRadius={20}
                                scale={scale ?? 1}
                                position={position}
                                onPositionChange={handlePositionChange}
                                rotate={0}
                            />
                        </div>
                        <div className="mt-2 w-full">
                            <OneHandleSlider
                                name="zoom"
                                label="Zoom"
                                min={1}
                                max={2}
                                step={0.1}
                                value={scale}
                                onChange={setScale}
                                className="w-full"
                                showInputField={false}
                            />
                        </div>
                    </div>
                ) : (
                    <div className="flex h-24 w-36 items-center justify-center rounded-lg bg-gray-200 text-sm text-gray-500">
                        No image selected
                    </div>
                )}
            </div>

            {/* Right: File Input and Upload Button */}
            <div className="flex flex-col items-center justify-center gap-4">
                <label
                    htmlFor="image-upload"
                    className="flex w-full cursor-pointer items-center justify-center gap-2 self-start rounded-md bg-primary px-5 py-2 font-semibold tracking-wide text-text transition-colors hover:bg-primary-200 hover:text-text"
                >
                    Choose File
                    <input
                        id="image-upload"
                        type="file"
                        onChange={handleNewImage}
                        accept="image/*"
                        className="hidden"
                    />
                </label>
                {image && (
                    <button
                        type="button"
                        onClick={handleSubmit}
                        className="flex w-full items-center justify-center gap-2 self-start rounded-md bg-primary px-5 py-2 font-semibold tracking-wide text-text transition-colors hover:bg-primary-200 hover:text-text"
                    >
                        Upload
                    </button>
                )}
            </div>
        </div>
    );
};

export default ProfilePictureUploader;
