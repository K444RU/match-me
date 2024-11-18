import Login from "./Login";

const Hero = ({
  title = "Amazing title",
  subtitle = "even more amazing subtitle."
}) => {
  return (
    <section className="bg-background py-20 mb-4">
      <div className="max-w-7x1 mx-auto px-4 sm:px-6 lg:px-8 flex flex-col items-center">
        <div className="text-center">
          <h1 className="text-4xl font-extrabold text-text sm:text-5x1 md: text-6x1">
            {title}
          </h1>
          <p className="my-4 text-xl text-text">{subtitle}</p>
          <div className="flex space-x-2">
          <button className="bg-primary-200 text-text hover:bg-primary-400 hover:text-text rounded-md px-5 py-2" >Learn more</button>
          <Login />
          </div>
        </div>
      </div>
    </section>
  )
}

export default Hero