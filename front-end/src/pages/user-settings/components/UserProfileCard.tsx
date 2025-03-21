import { HOBBIES } from '@/assets/hobbies';
import MotionSpinner from '@/components/animations/MotionSpinner';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import MultipleSelector, { Option } from '@/components/ui/multi-select';
import { Skeleton } from '@/components/ui/skeleton';
import { userService } from '@/features/user';
import { hobbiesById } from '@/lib/utils/dataConversion';
import ProfilePictureUploader from '@ui/forms/ProfilePictureUploader.tsx';
import { useContext, useEffect, useState } from 'react';
import { toast } from 'sonner';
import { SettingsContext } from '../SettingsContext';

const UserProfileCard = () => {
  const settingsContext = useContext(SettingsContext);

  const [firstName, setFirstName] = useState<string | null>('');
  const [lastName, setLastName] = useState<string | null>('');
  const [hobbies, setHobbies] = useState<Option[] | undefined>([]);
  const [alias, setAlias] = useState<string | null>('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!settingsContext?.settings) return;
    setFirstName(settingsContext.settings.firstName ?? '');
    setLastName(settingsContext.settings.lastName ?? '');
    setAlias(settingsContext.settings.alias ?? '');
    setHobbies(hobbiesById(settingsContext.settings.hobbies || []));
  }, [settingsContext?.settings]);

  if (!settingsContext || !settingsContext.settings) {
    return <div className="flex min-h-screen items-center justify-center text-text">Loading settings...</div>;
  }

  const handleUpdate = async () => {
    if (!settingsContext.settings) return;
    setLoading(true);
    try {
      if (!firstName || !lastName || !alias) return;
      await userService.updateProfileSettings({
        first_name: firstName,
        last_name: lastName,
        alias,
        hobbies: hobbies?.map((hobby) => parseInt(hobby.value)),
      });
      await settingsContext.refreshSettings();
      toast.success('Profile updated successfully');
    } catch (error) {
      toast.error('Failed to update profile');
      console.error('Error updating profile:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className="no-scrollbar h-[475px] w-full overflow-y-auto border-none shadow-none">
      <CardHeader>
        <CardTitle>Profile</CardTitle>
        <CardDescription>Edit your profile settings here.</CardDescription>
      </CardHeader>
      <CardContent>
        <form>
          <div className="grid w-full items-center gap-4">
            <div className="flex gap-2">
              <div className="flex flex-col space-y-1.5">
                <Label htmlFor="firstName">First Name</Label>
                {firstName !== undefined && firstName !== null ? (
                  <Input
                    id="firstName"
                    placeholder="First Name"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                  />
                ) : (
                  <Skeleton className="h-[40px] w-full rounded-md border-[#e5e7eb]" />
                )}
              </div>
              <div className="flex flex-col space-y-1.5">
                <Label htmlFor="lastName">Last Name</Label>
                {lastName !== undefined && lastName !== null ? (
                  <Input
                    id="lastName"
                    placeholder="Last Name"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                  />
                ) : (
                  <Skeleton className="h-[40px] w-full rounded-md border-[#e5e7eb]" />
                )}
              </div>
            </div>
            <div className="flex flex-col space-y-1.5">
              <label className="mb-1 text-sm font-medium text-gray-700">Profile Picture</label>
              <ProfilePictureUploader
                currentImage={settingsContext.settings.profilePicture ?? null}
                onUploadSuccess={async () => {
                  await settingsContext.refreshSettings();
                }}
              />
            </div>
            <div className="flex flex-col space-y-1.5">
              <Label htmlFor="alias">Alias</Label>
              {alias !== undefined && alias !== null ? (
                <Input id="alias" placeholder="Alias" value={alias} onChange={(e) => setAlias(e.target.value)} />
              ) : (
                <Skeleton className="h-[40px] w-full rounded-md border-[#e5e7eb]" />
              )}
            </div>
            <div className="flex flex-col space-y-1.5">
              <Label htmlFor="hobbies">Hobbies</Label>
              <MultipleSelector
                value={hobbies}
                onChange={setHobbies}
                placeholder="Select your hobbies..."
                defaultOptions={HOBBIES}
                groupBy="category"
                hideClearAllButton={true}
                maxSelected={5}
                hidePlaceholderWhenSelected={true}
              />
            </div>
          </div>
        </form>
      </CardContent>
      <CardFooter className="mt-auto flex justify-end">
        <Button onClick={handleUpdate} disabled={loading}>
          {loading ? (
            <>
              Updating <MotionSpinner />
            </>
          ) : (
            'Update'
          )}
        </Button>
      </CardFooter>
    </Card>
  );
};

export default UserProfileCard;
