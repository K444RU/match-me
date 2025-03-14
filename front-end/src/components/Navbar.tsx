import { Link } from 'react-router-dom';
import { useAuth } from '@/features/authentication';
import LinkButton from './ui/buttons/LinkButton';
import ThemeToggler from './ThemeToggler';

const Navbar = () => {
    const { user } = useAuth();
    return (
        <nav className="fixed w-full border-b border-background-500 bg-primary">
            <div className="max-w-7x1 mx-auto px-2 sm:px-6 lg:px-8">
                <div className="flex h-20 items-center justify-between">
                    <div className="flex w-full items-center justify-between">
                        <Link
                            to="/"
                            className="flex h-full cursor-pointer items-center justify-center px-2 md:block"
                        >
                            <span className="text-2xl font-bold text-text-50">
                                Blind
                            </span>
                        </Link>
                        <div className="md:ml-auto">
                            <div className="flex space-x-2">
                                <ThemeToggler />
                                {!user ? (
                                    <>
                                        <LinkButton
                                            to={'/login'}
                                            content={'Log in'}
                                        />
                                        <LinkButton
                                            to={'/register'}
                                            content={'Sign Up'}
                                        />
                                    </>
                                ) : (
                                    <>
                                        <LinkButton
                                            to={'/chats'}
                                            content={'Chats'}
                                        />
                                        <LinkButton
                                            to={'/logout'}
                                            content={'Log out'}
                                        />
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

export default Navbar;
