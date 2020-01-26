function init() 
    print('Hi, Electory!')
end

--[[
function key_event(ev)
    print('Key event: ' .. ev.getKey(ev))
end
]]--

electory.register_event_handler('init', init)
-- electory.register_event_handler('key_event', key_event)
