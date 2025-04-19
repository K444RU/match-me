import { userService } from '@/features/user';
import React, { useRef, useState } from 'react';
import ReactAvatarEditor from 'react-avatar-editor';
import { Button } from '../button';
import { Slider } from '../slider';

interface ProfilePictureUploaderProps {
  currentImage?: string | null;
  onUploadSuccess?: () => void;
}

export default function ProfilePictureUploader({ currentImage, onUploadSuccess }: ProfilePictureUploaderProps) {
  const [image, setImage] = useState<File | string | null>(null);
  const [scale, setScale] = useState<number | null>(1);
  const [position, setPosition] = useState<{ x: number; y: number }>({
    x: 0.5,
    y: 0.5,
  });
  const editorRef = useRef<ReactAvatarEditor>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const MAX_AVATAR_SIZE_MB = parseInt(import.meta.env.VITE_MAX_AVATAR_SIZE_MB);

  const handleNewImage = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      if (file.size > MAX_AVATAR_SIZE_MB * 1024 * 1024) {
        alert(`File size exceeds ${MAX_AVATAR_SIZE_MB} MB, please choose a smaller image.`);
        return;
      }
      if (!['image/png', 'image/jpeg'].includes(file.type)) {
        alert('Only PNG or JPEG images are allowed');
        return;
      }
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
        await userService.updateProfilePicture({ base64Image });
        setImage(null);
        if (onUploadSuccess) onUploadSuccess();
      } catch (error) {
        console.error('Upload error:', error);
        alert('Failed to upload picture.');
      }
    }
  };

  const handleRemove = async () => {
    try {
      await userService.updateProfilePicture({ base64Image: undefined });
      setImage(null);
      if (onUploadSuccess) onUploadSuccess();
    } catch (error) {
      console.error('Remove error:', error);
      alert('Failed to remove picture.');
    }
  };

  return (
    <div className="flex items-center gap-6 rounded-lg bg-card border p-6 shadow-md">
      {/* Left: Image Preview */}
      <div className="flex flex-col items-center">
        {image ? (
          <div className="flex flex-col items-center">
            <div className="flex size-48 items-center justify-center rounded-lg">
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
              <Slider
                min={1}
                max={2}
                step={0.1}
                value={[scale ?? 1]}
                onValueChange={(value) => setScale(value[0])}
                className="w-full"
              />
            </div>
          </div>
        ) : currentImage ? (
          <img src={currentImage} alt="Profile Picture" className="size-48 rounded-lg object-cover" />
        ) : (
          <div className="flex h-24 w-36 items-center justify-center rounded-lg text-sm bg-foreground/5">
            No image selected
          </div>
        )}
      </div>

      {/* Right: Buttons */}
      <div className="flex flex-col items-center justify-center gap-4">
        {image ? (
          <Button
            type="button"
            onClick={handleSubmit}
          >
            Upload
          </Button>
        ) : currentImage ? (
          <>
            <Button
              type="button"
              onClick={handleRemove}
              variant="destructive"
            >
              Remove
            </Button>
            <Button
              type="button"
              onClick={() => fileInputRef.current?.click()}
            >
              Change
            </Button>
          </>
        ) : (
          <Button
            type="button"
            onClick={() => fileInputRef.current?.click()}
          >
            Choose File
          </Button>
        )}
        <input ref={fileInputRef} type="file" onChange={handleNewImage} accept="image/*" className="hidden" />
      </div>
    </div>
  );
};
