import Login from "../components/Login";

const Navbar = () => {
  return (
    <nav className="bg-primary border-b border-background-500">
      <div className="mx-auto max-w-7x1 px-2 sm:px-6 lg:px-8">
        <div className="flex h-20 items-center justify-between">
          <div className="flex flex-1 items-center justify-center md:items-stretch md:justify-start">
            <div className="flex flex-shrink-0 items-center mr-4">
            <h1 className="hidden md:block text-text-50 text-2xl font-bold ml-2">Blind</h1>
            </div>
            <div className="md:ml-auto">
              <div className="flex space-x-2">
            <Login isLogin={true} />
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>
  )
}

export default Navbar