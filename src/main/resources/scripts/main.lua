function register_blocks()
    electory.register_block('cobblestone', {
        sprite_name = '/img/blocks/cobblestone.png'
    })
    
    electory.register_block('planks', {
        sprite_name = '/img/blocks/planks.png'
    })
    
    electory.register_block('dirt', {
        sprite_name = '/img/blocks/dirt.png'
    })
    
    electory.register_block('sand', {
        sprite_name = '/img/blocks/sand.png',
        sound = 'sfx/break/sand1.ogg'
    })
    
    electory.register_block('stone', {
        sprite_name = '/img/blocks/stone.png'
    })
    
    electory.register_block('gravel', {
        sprite_name = '/img/blocks/gravel.png'
    })
    
    electory.register_block('glass', {
        sprite_name = '/img/blocks/glass.png',
        solid = false
    })
    electory.register_block('rootstone', {
        sprite_name = '/img/blocks/rootstone.png',
        breakable = false
    })
end

electory.register_event_handler('register_blocks', register_blocks)
