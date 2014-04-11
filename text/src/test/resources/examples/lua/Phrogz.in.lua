require 'os'
local c = os.clock
function r1(i,n) return math.floor(i/n+0.5)*n      end
function r2(i,n) local m=n/2; return i+m - (i+m)%n end

local t=c(); for i=1,12345678 do r1(i,20) end; print(c()-t) --> 2.007
local t=c(); for i=1,12345678 do r2(i,20) end; print(c()-t) --> 1.45
