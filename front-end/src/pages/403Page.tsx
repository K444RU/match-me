import { Link } from 'react-router-dom';

export default function ForbiddenPage() {
  return (
    <div className="flex h-screen w-full flex-col items-center justify-center space-x-4 text-center text-4xl tracking-tight dark:text-white sm:text-5xl lg:text-6xl">
      <span className="font-extrabold">403</span>
      <span className="">Locked SIR</span>
      <Link to="/">Go Home</Link>
    </div>
  );
}
