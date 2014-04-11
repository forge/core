-- Fred, tiny wimp program using error dialogue
require "wimp.task"
do
 local dim,! in riscos
 local buffer, wimp_msgs = dim(256), dim(4)
 local title$ = dim "Fred\r"
 local ask = "Count is %d. CANCEL to stop now?"
 local goodbye = "You reached %d. Goodbye."
 local flags = 23  -- for dialogue window
 ![wimp_msgs] = 0  -- no wimp messages
 fred = task.new(title$,wimp_msgs,buffer)   -- create task "fred"
 in fred do
  count = 0                      -- give it a counter
  handler[0] = \ (self)      -- give it a null-event handler
    count = count + 1
    local click = self:report(ask:format(count),flags)
    => (click ~= 1)          -- OK button to continue
  end -- handler
  preclosedown = \ (self)     -- give it a farewell action
     self:report(goodbye:format(count))
  end -- preclosedown
 end -- do
 fred:init()                   -- register it
 fred:run()                    -- run it
end -- do