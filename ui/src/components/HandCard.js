import React, {useRef} from "react";
import cardImages from "../img/cards/cards";
import {useDrag} from "react-dnd";
import {useGameState} from "../context/GameState";

function HandCard(props) {
    const {registerDrop} = useGameState()
    const [,drag] = useDrag({
        type: 'Card',
        item: props,
        end: (item, monitor) => {
            if (!monitor.didDrop()) {
                registerDrop(item, null)
            }
        }
    })

    const refContainer = useRef(null)

    let marginTop = '10px'
    let marginLeft = '10px'

    let style = {width: '70px', height: '100px', marginLeft: marginLeft, marginTop:marginTop}

    return <div ref={drag}>
        <img style={style}
             key={props.index} id={props.index}
             src={cardImages[props.card]} title={props.card} alt={props.card}
             ref={refContainer}
        />

    </div>

}

export default HandCard;
