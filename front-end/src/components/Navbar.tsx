import { useAuth } from '@/features/authentication';
import { cn } from '@/lib/utils';
import { Link } from 'react-router-dom';
import ThemeToggler from './ThemeToggler';
import LinkButton from './ui/buttons/LinkButton';

interface NavbarProps {
  className?: string;
}

export default function Navbar({ className }: NavbarProps) {
  const { user } = useAuth();
  return (
    <nav className={cn('w-full border-b border-background/50 bg-primary z-20', className)}>
      <div className="max-w-7x1 mx-auto px-2 sm:px-6 lg:px-8">
        <div className="flex h-20 items-center justify-between">
          <div className="flex w-full items-center justify-between">
            <Link to="/" className="flex h-full cursor-pointer items-center justify-center px-2 md:block">
              <span className="text-2xl font-bold ">Blind</span>
            </Link>
            <div className="md:ml-auto">
              <div className="flex space-x-2">
                <ThemeToggler />
                {!user ? (
                  <>
                    <LinkButton to={'/login'} content={'Log in'} />
                    <LinkButton to={'/register'} content={'Sign Up'} />
                  </>
                ) : (
                  <>
                    <LinkButton to={'/chats'} content={'Chats'} />
                    <LinkButton to={'/logout'} content={'Log out'} />
                  </>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};
