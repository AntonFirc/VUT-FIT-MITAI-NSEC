# FE -> BE communication

### General e-shop actions (non-logged user)
[ ] -- List products (filtered/non-filtered) (submit product search)   : GET
[ ] -- Show product categories                                         : GET
[ ] -- Show product details (description, price, in-stock count etc.)  : GET
[ ] -- Show subset of products (newest, in certain category)           : GET

### User account actions (non-logged user)
[ ] -- Login                         : GET/POST?
[ ] -- Register                      : POST
[ ] -- Delete/cancel account         : POST
[ ] -- Reset password                : GET/POST?
[ ] -- Confirm passowrd reset        : POST
[ ] -- Update profile (billing) info : POST

### Shopping actions
[ ] -- Add an item to the cart                                : POST
[ ] -- Remove an item from the cart                           : POST
[ ] -- Add a favourite item                                   : POST
[ ] -- Remove a favourite item                                : POST
[ ] -- Show the cart contents                                 : GET
[ ] -- Proceed to purchase (show goods details and total sum) : GET
[ ] -- Create an order (finalize the purchase)                : POST
[ ] -- Cancel an order                                        : POST
[ ] -- Check order status                                     : GET
[ ] -- Create a clain ("reklamacia")                          : POST
[ ] -- Show a status of a claim                               : GET

### Employee actions
[ ] -- Check the order status (with employee details) : GET
[ ] -- Change the order status                        : POST
[ ] -- Show a claim                                   : GET
[ ] -- Respond to a claim                             : POST
[ ] -- Change a claim status (update, cancel)         : POST

### Administrator actions
[ ] -- Display employee activity                          : GET
[ ] -- Display e-shop statistics                          : GET
[ ] -- Add a discount (particular product, product group) : POST
[ ] -- Remove a discount                                  : POST
[ ] -- Change a discount                                  : POST
[ ] -- Update a number of in-stock items                  : POST
[ ] -- Add/remove a new product                           : POST
[ ] -- Update product details/price                       : POST
[ ] -- Add/remove a product category                      : POST
[ ] -- Update a product category details                  : POST
[ ] -- Disable a user account                             : POST
[ ] -- Modify rights of the user/user-group               : POST

