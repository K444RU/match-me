import MotionSpinner from '@/components/animations/MotionSpinner';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Skeleton } from '@/components/ui/skeleton';
import { useAuth } from '@/features/authentication';
import { userService } from '@/features/user';
import { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { SettingsContext } from '../SettingsContext';

const UserAccountCard = () => {
  const settingsContext = useContext(SettingsContext);
  const [email, setEmail] = useState<string>();
  const [countryCode, setCountryCode] = useState<string>();
  const [number, setNumber] = useState<string>();
  const [loading, setLoading] = useState(false);
  const { logout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!settingsContext?.settings) return;

    setEmail(settingsContext.settings.email ?? '');
    const [code, phoneNumber] = (settingsContext.settings.number ?? '').split(' ');
    setCountryCode(code ?? '');
    setNumber(phoneNumber ?? '');
  }, [settingsContext?.settings]);

  const handleUpdate = async () => {
    if (!settingsContext?.settings) return;

    setLoading(true);
    try {
      if (!email || !number || !countryCode) return;
      await userService.updateAccountSettings({
        email,
        number: `${countryCode} ${number}`,
      });
      if (settingsContext.settings.email !== email) {
        logout();
        navigate('/login');
      }
      await settingsContext.refreshSettings();
      toast.success('Account updated successfully');
    } catch (error) {
      toast.error('Failed to update account');
      console.error('Error updating account:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className="h-[475px] w-full border-none shadow-none">
      <CardHeader>
        <CardTitle>Account</CardTitle>
        <CardDescription>Edit your account settings here.</CardDescription>
      </CardHeader>
      <CardContent>
        <form>
          <div className="grid w-full items-center gap-4">
            <div className="flex flex-col space-y-1.5">
              <Label htmlFor="email">Email</Label>
              {email !== undefined && email !== null ? (
                <Input id="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
              ) : (
                <Skeleton className="h-[40px] w-full" />
              )}
            </div>
            <div className="flex gap-4">
              <div className="flex w-1/3 flex-col space-y-1.5">
                <Label htmlFor="countryCode">Country Code</Label>
                {countryCode !== undefined && countryCode !== null ? (
                  <Input
                    id="countryCode"
                    placeholder="Country Code"
                    value={countryCode}
                    onChange={(e) => setCountryCode(e.target.value)}
                  />
                ) : (
                  <Skeleton className="h-[40px] w-full" />
                )}
              </div>
              <div className="flex w-2/3 flex-col space-y-1.5">
                <Label htmlFor="phoneNumber">Phone Number</Label>
                {number !== undefined && number !== null ? (
                  <Input
                    id="phoneNumber"
                    placeholder="Phone Number"
                    value={number}
                    onChange={(e) => setNumber(e.target.value)}
                  />
                ) : (
                  <Skeleton className="h-[40px] w-full" />
                )}
              </div>
            </div>
          </div>
        </form>
      </CardContent>
      <CardFooter className="flex justify-end">
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

export default UserAccountCard;
