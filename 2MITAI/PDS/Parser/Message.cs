namespace FTPparser
{
    public class Message
    {
        private string type; // determines if response, request or error therefore which attributes are used
        /* attributes used if request type */
        private string command; // command code (3/4 chars)
        private string commandArgument; // command argument, optional attribute

        /* attributes used if response type */
        private int responseCode; // 3-digit response code
        private string responseMessage; // addtional response info from server
        
        private string errorMessage; // if parsing error occurs, error message is stored here

        public Message(string type)
        {
            this.type = type;
            this.command = null;
            this.commandArgument = null;
            this.responseCode = -1;
            this.responseMessage = null;
        }
        
        public string Type
        {
            get => type;
            set => type = value;
        }
        
        public string Command
        {
            get => command;
            set => command = value;
        }

        public string CommandArgument
        {
            get => commandArgument;
            set => commandArgument = value;
        }
        
        public int ResponseCode
        {
            get => responseCode;
            set => responseCode = value;
        }

        public string ResponseMessage
        {
            get => responseMessage;
            set => responseMessage = value;
        }

        public string ErrorMessage
        {
            get => errorMessage;
            set => errorMessage = value;
        }
    }
}