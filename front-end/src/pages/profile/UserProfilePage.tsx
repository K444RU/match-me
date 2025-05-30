import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {useAuth} from '@/features/authentication';
import PageNotFound from '@/pages/404Page.tsx';
import {ArrowLeftIcon, CameraIcon} from 'lucide-react';
import {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import { meService, userService } from '@/features/user';
import UserAvatar from '../chats/components/UserAvatar';
import {HobbyResponseDTO, ProfileResponseDTO} from '@/api/types';

export default function UserProfilePage() {
  const { user } = useAuth();
  const { id } = useParams<{ id?: string }>();
  const navigate = useNavigate();

  const [notFound, setNotFound] = useState(false);
  const [userData, setUserData] = useState<ProfileResponseDTO | null>(null);
  const [isOwner, setIsOwner] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!user) return;

    const fetchUserData = async () => {
      try {
        setLoading(true);

        let response: ProfileResponseDTO;

        if (id) {
          const targetId = parseInt(id, 10);
          if (isNaN(targetId)) {
            setNotFound(true);
            return;
          }
          response = await userService.getUserProfile(targetId);
        } else {
          response = await meService.getUserProfile();
        }

        setUserData(response);
        setIsOwner(!id || id === (user.id ?? '').toString());
      } catch (err: unknown) {
        const errorObj = err as { response?: { status: number } };
        if (errorObj.response?.status === 404) {
          setNotFound(true);
        } else {
          setError('Error loading profile.');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, [id, user]);

  if (!user) {
    return <div className="flex flex-1 items-center justify-center">Please log in to view this page.</div>;
  }

  if (loading) {
    return <div className="flex flex-1 items-center justify-center">Loading...</div>;
  }

  if (notFound) return <PageNotFound />;

  if (error) {
    return <div className="flex flex-1 items-center justify-center">{error}</div>;
  }

  if (!userData) return null;

  return (
    <div className="relative flex flex-1 flex-col overflow-auto">
      <div className="bg-background w-full h-48 relative">
        <div className="relative h-48 w-full bg-accent/20">
          {isOwner && (
            <Button variant="secondary" size="icon" className="absolute right-4 top-4 rounded-full">
              <CameraIcon className="size-5" />
            </Button>
          )}
        </div>
      </div>

      <div className="mx-auto flex w-full max-w-6xl flex-1 flex-col px-4 pb-16 md:flex-row md:gap-6 md:px-4 lg:px-8">
        <div className="relative -mt-16 flex flex-col items-center md:w-1/2">
          <Card className="w-full">
            <CardContent className="p-6 text-center">
              <UserAvatar
                name={user.firstName}
                profileSrc={userData.profilePicture}
                avatarClassName="mx-auto mb-4 size-36 border-4 border-background ring-2 ring-primary"
                fallbackClassName="text-2xl font-semibold"
              />
              <h1 className="text-2xl font-semibold">
                {userData.firstName} {userData.lastName}
              </h1>
              {userData.alias && <p className="text-sm text-muted-foreground">@{userData.alias}</p>}
              {isOwner && user.email && <p className="mt-2 text-sm text-muted-foreground">{user.email}</p>}
            </CardContent>
          </Card>
        </div>

        <div className="mt-6 flex-1 space-y-6 md:mt-0">
          <Card>
            <CardHeader>
              <CardTitle>About Me</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="leading-relaxed text-sm text-muted-foreground whitespace-pre-wrap">
                {userData.aboutMe || (isOwner ? 'You haven\'t added anything here yet.' : 'No information provided.')}
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Hobbies</CardTitle>
            </CardHeader>
            <CardContent>
              <ul className="list-inside list-disc space-y-1">
                {userData.hobbies && userData.hobbies.length > 0 ? (
                  userData.hobbies.map((hobby: HobbyResponseDTO, index: number) => <li key={index}>{hobby.name}</li>)
                ) : (
                  <li>No hobbies listed</li>
                )}
              </ul>
            </CardContent>
          </Card>
        </div>
      </div>

      <div className="absolute top-4 left-4 z-10">
        <Button variant="default" onClick={() => navigate(-1)} className="gap-2">
          <ArrowLeftIcon className="size-4" /> Back
        </Button>
      </div>
    </div>
  );
};
