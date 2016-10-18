/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey.karakovskiy@gmail.com
 * Date: Apr 8, 2009
 * Time: 4:03:46 AM
 */

public class OwnAgent extends BasicMarioAIAgent implements Agent
{
int trueJumpCounter = 0;
int trueSpeedCounter = 0;
boolean shouldJumpOntoHall = false;
boolean isJumping = false;

public OwnAgent()
{
    super("OwnAgent");
    reset();
}

public void reset()
{
	action = new boolean[Environment.numberOfKeys];
	action[Mario.KEY_RIGHT] = true;
}

public boolean isObstacle(int r, int c){
	return getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.BRICK
			|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH
			|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.FLOWER_POT_OR_CANNON
			|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.LADDER;
}

private boolean isOverHall(int r, int c) {
	for (int  i = 1; i <= 9; i++) {
		if (isObstacle(r + i, c)) {
			return false;
		}
	}
	return true;
}

private boolean shouldJump () {
	if (isMarioOnGround && !isMarioAbleToJump) {
		// 着地したら一旦ボタンを離す
		return false;
	}
	if (!isMarioOnGround) {
		// ジャンプ中はAボタンを押したままで大ジャンプする
		return true;
	}
	return isObstacle(marioEgoRow, marioEgoCol + 1) ||
			isObstacle(marioEgoRow, marioEgoCol + 2) ||
			getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) != Sprite.KIND_NONE ||
			getEnemiesCellValue(marioEgoRow, marioEgoCol + 2) != Sprite.KIND_NONE ||
			getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol + 1) == 0;
}

public boolean[] getAction()
{
	// 穴の手前から走り始めてダッシュジャンプで越える
	action[Mario.KEY_SPEED] = isTowardHole();

	if (shouldAvoidHall()) {
		action[Mario.KEY_LEFT] = true;
		action[Mario.KEY_RIGHT] = false;
		action[Mario.KEY_JUMP] = false;
	} else if (shouldJump()) {
		action[Mario.KEY_LEFT] = false;
		action[Mario.KEY_RIGHT] = true;
		action[Mario.KEY_JUMP] = true;
	} else {
		action[Mario.KEY_LEFT] = false;
		action[Mario.KEY_RIGHT] = true;
		action[Mario.KEY_JUMP] = false;
	}
	 return action;
}

private boolean isTowardHole() {
	return isOverHall(marioEgoRow, marioEgoCol + 1) ||
		isOverHall(marioEgoRow, marioEgoCol + 2) ||
		isOverHall(marioEgoRow, marioEgoCol + 3) ||
		isOverHall(marioEgoRow, marioEgoCol + 4);
}

private boolean shouldAvoidHall() {
	if (isMarioAbleToJump || !isMarioOnGround) {
		return false;
	}
	return !isOverHall(marioEgoRow, marioEgoCol) && isOverHall(marioEgoRow, marioEgoCol + 1);
}
}