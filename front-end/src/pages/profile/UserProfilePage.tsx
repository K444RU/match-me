import defaultProfilePicture from '@/assets/defaultProfilePicture.png';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { useAuth } from '@/features/authentication';
import PageNotFound from '@/pages/404Page.tsx';
import axios from 'axios';
import { CameraIcon } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

interface UserProfile {
  id: string;
  firstName: string;
  lastName: string;
  alias?: string;
  email?: string;
  profilePicture?: string;
  aboutMe?: string;
  hobbies?: string[];
}

const UserProfilePage = () => {
  const { user: authUser } = useAuth();
  const { id } = useParams<{ id?: string }>();
  const navigate = useNavigate();

  const [notFound, setNotFound] = useState(false);
  const [userData, setUserData] = useState<UserProfile | null>(null);
  const [isOwner, setIsOwner] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!authUser) return;

    const fetchUserData = async () => {
      try {
        setLoading(true);
        const endpoint = id ? `/api/users/${id}` : '/api/me';
        const response = await axios.get<UserProfile>(endpoint, {
          headers: { Authorization: `Bearer ${authUser.token}` },
        });
        setUserData(response.data);
        setIsOwner(!id || id === (authUser.id ?? '').toString());
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
  }, [id, authUser]);

  if (!authUser) {
    return (
      <div className="flex min-h-screen items-center justify-center text-text">Please log in to view this page.</div>
    );
  }

  if (loading) {
    return <div className="flex min-h-screen items-center justify-center text-text">Loading...</div>;
  }

  if (notFound) return <PageNotFound />;

  if (error) {
    return <div className="flex min-h-screen items-center justify-center text-text">{error}</div>;
  }

  if (!userData) return null;

  return (
    <div className="flex min-h-screen w-full flex-col bg-background text-text">
      {/* Background Image Section with Upload Icon / Future possible implementation */}
      <div className="relative h-48 w-full bg-muted">
        {isOwner && (
          <button className="text-primary-foreground hover:bg-primary/90 absolute right-2 top-2 rounded-full bg-primary p-2 opacity-70">
            <CameraIcon className="size-6" />
          </button>
        )}
      </div>

      <div className="flex flex-1 flex-col md:flex-row">
        {/* Left Column: User Data */}
        <div className="flex w-full flex-col items-center bg-background-400 p-4 md:w-1/3">
          <Avatar className="mb-4 size-36">
            <AvatarImage src={userData.profilePicture || defaultProfilePicture} alt={userData.firstName} />
            <AvatarFallback>
              {userData.firstName?.[0]}
              {userData.lastName?.[0]}
            </AvatarFallback>
          </Avatar>
          <div className="rounded-lg bg-background-200 p-4">
            <h1 className="text-center text-2xl font-semibold">
              {userData.firstName} {userData.lastName}
            </h1>
            {userData.alias && <p className="text-center text-sm text-muted-foreground">{userData.alias}</p>}
            {isOwner && userData.email && <p className="text-center text-sm text-muted-foreground">{userData.email}</p>}
          </div>
        </div>

        {/* Right Column: Additional Data */}
        <div className="w-full p-4 md:w-2/3">
          <section className="mb-6">
            <h2 className="mb-2 text-xl font-semibold">About Me</h2>
            <p className="leading-relaxed">{userData.aboutMe || 'Not set'}</p>
          </section>
          <section>
            <h2 className="mb-2 text-xl font-semibold">Hobbies</h2>
            <ul className="list-disc pl-5">
              {userData.hobbies && userData.hobbies.length > 0 ? (
                userData.hobbies.map((hobby: string, index: number) => <li key={index}>{hobby}</li>)
              ) : (
                <li>No hobbies listed</li>
              )}
            </ul>
          </section>
        </div>
      </div>

      {/* Back Button */}
      <button
        onClick={() => navigate(-1)}
        className="text-primary-foreground hover:bg-primary/90 fixed bottom-4 left-4 z-10 w-48 rounded bg-primary px-4 py-2"
      >
        Back
      </button>
    </div>
  );
};

export default UserProfilePage;
