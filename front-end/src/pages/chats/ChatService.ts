const sendMessage = (message: string, to: number, from: string) => {
    // to = connectionId
    // from = authToken which will get turned into userId in backend
    // We don't want to let the user pick sender himself.
    console.log(message);
    console.log(to);
    console.log(from);
};

export default sendMessage;
